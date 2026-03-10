package com.kustaurant.crawler.aianalysis.adapter.out.messaging.redis;

import com.kustaurant.crawler.aianalysis.adapter.out.messaging.Message;
import com.kustaurant.crawler.global.util.JsonUtils;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessageReader;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamsReader implements MessageReader {

    private final StringRedisTemplate redisTemplate;

    @Override
    public <T> Optional<Message<T>> read(
            String topic,
            String group,
            String consumerName,
            Class<T> type
    ) {
        List<MapRecord<String, Object, Object>> messages =
                redisTemplate.opsForStream().read(
                        Consumer.from(group, consumerName),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create(topic, ReadOffset.lastConsumed())
                );

        if (messages == null || messages.isEmpty()) {
            return Optional.empty();
        }

        MapRecord<String, Object, Object> record = messages.get(0);
        String payloadJson = (String) record.getValue().get("payload");
        if (payloadJson == null || payloadJson.isBlank()) {
            ack(topic, group, record.getId());
            log.warn("skip blank payload message. recordId={}", record.getId());
            return Optional.empty();
        }
        T payload = JsonUtils.deserialize(payloadJson, type);

        return Optional.of(new Message<>(
                payload,
                () -> ack(topic, group, record.getId())
        ));
    }

    public void ack(String topic, String group, RecordId id) {
        redisTemplate.opsForStream().acknowledge(topic, group, id);
    }
}
