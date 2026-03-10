package com.kustaurant.crawler.aianalysis.adapter.out.messaging.redis;

import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagePublisher;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamsPublisher implements MessagePublisher<String> {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void publish(String topic, String payload) {
        RecordId id = redisTemplate.opsForStream()
                .add(
                        StreamRecords
                                .string(Map.of("payload", payload))
                                .withStreamKey(topic)
                );
        String idValue = Optional.ofNullable(id).map(RecordId::getValue).orElse("");
//        log.info("[Stream Id: {}] 메시지 발행됨. 메시지 내용: {}", idValue, payload);
    }

    @Override
    public void publish(String topic, List<String> payloads) {
        for (String payload : payloads) {
            publish(topic, payload);
        }
    }
}
