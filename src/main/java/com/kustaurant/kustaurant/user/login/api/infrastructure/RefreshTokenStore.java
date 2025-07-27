package com.kustaurant.kustaurant.user.login.api.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_FMT = "refresh:%d";

    public void save(Long userId, String refreshToken, Duration ttl) {
        redisTemplate.opsForValue().
                set(KEY_FMT.formatted(userId), refreshToken, ttl);
    }

    public String get(Long userId) {
        return redisTemplate.opsForValue().get(KEY_FMT.formatted(userId));
    }

    public void delete(Long userId) {
        redisTemplate.delete(KEY_FMT.formatted(userId));
    }
}
