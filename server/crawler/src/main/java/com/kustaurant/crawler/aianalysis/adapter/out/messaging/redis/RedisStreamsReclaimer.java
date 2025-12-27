package com.kustaurant.crawler.aianalysis.adapter.out.messaging.redis;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.kustaurant.crawler.global.util.JsonUtils;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XAutoClaimArgs;
import io.lettuce.core.XAutoClaimArgs.Builder;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.models.stream.ClaimedMessages;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class RedisStreamsReclaimer<T> {

    private final StringRedisTemplate redisTemplate;
    private final AtomicBoolean running = new AtomicBoolean(false);

    // 구성값
    private final String topic;
    private final String group;
    private final String consumerName;
    private final long minIdleMs;
    private final int batch;
    private final int maxRetries;
    private final String dlq;
    private volatile String cursor = "0-0";

    private final Class<T> type;
    private final Consumer<T> handler;

    public RedisStreamsReclaimer(
            StringRedisTemplate redisTemplate,
            Class<T> type,
            Consumer<T> handler,
            String topic,
            String group,
            String consumerName,
            String dlq
    ) {
        this.redisTemplate = redisTemplate;

        this.topic = topic;
        this.group = group;
        this.consumerName = consumerName;
        this.minIdleMs = 300_000L;
        this.batch = 10;
        this.maxRetries = 3;
        this.dlq = dlq;

        this.type = type;
        this.handler = handler;
    }

//    @Scheduled(fixedDelayString = "30000") // 30초마다
    public void start() {
        if (!running.compareAndSet(false, true)) return;

//        boolean locked = false;
//        try {
//            locked = AiAnalysisSubscriber.writeLock().tryLock();
//            if (!locked) {
//                return;
//            }
//            reclaimAndRetry();
//        } finally {
//            if (locked) AiAnalysisSubscriber.writeLock().unlock();
//            running.set(false);
//        }
    }

    @SuppressWarnings("unchecked")
    private void reclaimAndRetry() {
        LettuceConnection lc =
                (LettuceConnection) redisTemplate.getConnectionFactory().getConnection();

        Object nativeObj = lc.getNativeConnection();

        StatefulRedisConnection<byte[], byte[]> stateful;
        if (nativeObj instanceof StatefulRedisConnection) {
            stateful = (StatefulRedisConnection<byte[], byte[]>) nativeObj;
        } else if (nativeObj instanceof RedisAsyncCommands) {
            stateful = ((RedisAsyncCommands<byte[], byte[]>) nativeObj).getStatefulConnection();
        } else {
            throw new IllegalStateException("Unsupported native connection type: " + nativeObj.getClass());
        }

        RedisCommands<byte[], byte[]> sync = stateful.sync();

        byte[] key = topic.getBytes(UTF_8);
        byte[] groupBytes = group.getBytes(UTF_8);
        byte[] consumerBytes = consumerName.getBytes(UTF_8);
        XAutoClaimArgs<byte[]> args = Builder.xautoclaim(
                io.lettuce.core.Consumer.from(groupBytes, consumerBytes), minIdleMs, cursor);
        ClaimedMessages<byte[], byte[]> out = sync.xautoclaim(key, args);

        cursor = out.getId();
        List<StreamMessage<byte[], byte[]>> messages = out.getMessages();

        List<MapRecord<String, String, String>> records = new ArrayList<>();
        for (StreamMessage<byte[], byte[]> m : messages) {
            String id = m.getId();
            Map<byte[], byte[]> body = m.getBody();

            Map<String, String> map = new LinkedHashMap<>(Math.max(4, body.size() * 2));
            for (Map.Entry<byte[], byte[]> e : body.entrySet()) {
                String k = e.getKey() == null ? null : new String(e.getKey(), UTF_8);
                String v = e.getValue() == null ? null : new String(e.getValue(), UTF_8);
                map.put(k, v);
            }
            MapRecord<String, String, String> rec = MapRecord.create(topic, map)
                    .withId(RecordId.of(id));
            records.add(rec);
        }

        if (records.isEmpty()) {
            return;
        }

        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
        for (MapRecord<String, String, String> rec : records) {
            RecordId id = rec.getId();
            String payload = rec.getValue().get("payload");

            try {
                long deliveries = getDeliveries(topic, group, id.getValue());
                if (deliveries > maxRetries) {
//                    moveToDlqAndAck(ops, rec, deliveries, "max-retries-exceeded");
                    continue;
                }
                log.info("[{}][{}][{}] retry start. retry count={}, message={}", topic, group, consumerName, deliveries, payload);

                // 비즈니스 재처리
                T dto = JsonUtils.deserialize(payload, type);
                handler.accept(dto);

                // 성공 시 ACK
                ops.acknowledge(topic, group, id);
            } catch (Exception e) {
                log.warn("[{}][{}][{}] retry failed id={}", topic, group, consumerName, id, e);
            }
        }
    }

    /** 특정 메시지 id의 배달 횟수 조회 (XPENDING range 1건) */
    private Long getDeliveries(String topic, String group, String id) {
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            PendingMessages msgs = connection.streamCommands().xPending(
                    topic.getBytes(UTF_8),
                    group,
                    Range.closed(id, id),
                    1L
            );
            if (msgs != null && !msgs.isEmpty()) {
                return msgs.get(0).getTotalDeliveryCount() - 1;
            }
            // 조회 실패 시 보수적으로 1회로 간주
            return 1L;
        });
    }

    /** DLQ로 복제 후 원본 ACK */
    private void moveToDlqAndAck(
            StreamOperations<String, String, String> ops,
            MapRecord<String, String, String> rec,
            long deliveries,
            String reason
    ) {
        ops.add(StreamRecords.newRecord()
                .in(dlq)
                .ofMap(Map.of(
                        "original_id", rec.getId().toString(),
                        "payload", rec.getValue().get("payload"),
                        "deliveries", String.valueOf(deliveries),
                        "reason", reason
                )));
        ops.acknowledge(topic, group, rec.getId());
    }
}
