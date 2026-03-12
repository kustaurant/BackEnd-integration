package com.kustaurant.kustaurant.common.discordAlert;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class AlertRateLimiter {
    private static final long WINDOW_MINUTES = 5L;

    private final Cache<String, Long> lastSentCache = Caffeine.newBuilder()
            .expireAfterWrite(WINDOW_MINUTES, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public boolean allow(String key) {
        Long lastSentAt = lastSentCache.getIfPresent(key);
        if (lastSentAt != null) {
            return false;
        }

        lastSentCache.put(key, System.currentTimeMillis());
        return true;
    }
}
