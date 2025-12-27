package com.kustaurant.crawler.aianalysis.adapter.out.messaging.redis;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.lettuce.core.RedisBusyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamsUtils {

    private final RedisConnectionFactory cf;

    public void createStreamAndGroupIfNotExists(String key, String group) {
        try (RedisConnection conn = cf.getConnection()) {
            byte[] k = key.getBytes();
            // 기존 스트림 관련 삭제
            conn.keyCommands().del(k);
            // 그룹 생성
            conn.streamCommands().xGroupCreate(
                    k,
                    group,
                    ReadOffset.latest(), // 0: 처음 메시지부터 읽기, $: 연결된 이후 메시지 부터 읽기
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
