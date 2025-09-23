package com.kustaurant.mainapp.user.login.api.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_FMT = "refresh:%d";

    public void save(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().
                set(KEY_FMT.formatted(userId), refreshToken, ttl);
    }

    public void saveField(Long userId, String envKey,
                          String refreshToken, Duration ttl) {

        String key = KEY_FMT.formatted(userId);
        String value = refreshToken + "|" + Instant.now().toEpochMilli();

        redisTemplate.opsForHash().put(key, envKey, value);
        redisTemplate.expire(key, ttl);
    }

    public String getField(Long userId, String envKey) {
        Object v = redisTemplate.opsForHash().get(KEY_FMT.formatted(userId), envKey);
        return v != null ? v.toString().split("\\|")[0] : null;   // token 부분만
    }

    /** 사용자‑환경별 전체 맵 반환: {envKey -> token|timestamp} */
    public Map<String, String> getAll(Long userId) {
        Map<Object,Object> raw = redisTemplate.opsForHash().entries(KEY_FMT.formatted(userId));
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));
    }

    public void deleteField(Long userId, String envKey) {
        redisTemplate.opsForHash().delete(KEY_FMT.formatted(userId), envKey);
    }

    /** 가장 오래된 envKey 찾아서 반환 */
    public Optional<String> findOldestKey(Long userId) {
        return getAll(userId).entrySet().stream()
                .min((e1, e2) -> {
                    long t1 = ts(e1.getValue().toString());
                    long t2 = ts(e2.getValue().toString());
                    return Long.compare(t1, t2);
                })
                .map(e -> e.getKey().toString());
    }

    private long ts(String value) {
        // token|timestamp
        String[] parts = value.split("\\|");
        return (parts.length == 2) ? Long.parseLong(parts[1]) : 0L;
    }

    // --------------------------- //

    public String get(Long userId) {
        return redisTemplate.opsForValue().get(KEY_FMT.formatted(userId));
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_FMT.formatted(userId));
    }
}
