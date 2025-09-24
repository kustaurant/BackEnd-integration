package com.kustaurant.dataprocessor.infrastructure.messaging.redis;

import io.lettuce.core.RedisBusyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamsUtils {

    private final RedisConnectionFactory cf;

    public void createStreamAndGroupIfNotExists(String key, String group) {
        var conn = cf.getConnection();
        var sc = conn.streamCommands();
        try {
            sc.xGroupCreate(
                    key.getBytes(),
                    group,
                    ReadOffset.from("0"), // 0: 처음 메시지부터 읽기, $: 연결된 이후 메시지 부터 읽기
                    true // 없을 경우 스트림을 만듦
            );
        } catch (DataAccessException e) {
            if (e.getCause() instanceof RedisBusyException
                    && e.getCause().getMessage().contains("BUSYGROUP")) {
                log.info("key&group 조합이 이미 있습니다. (key-{}, group-{})", key, group);
            } else {
                throw e;
            }
        }
    }
}
