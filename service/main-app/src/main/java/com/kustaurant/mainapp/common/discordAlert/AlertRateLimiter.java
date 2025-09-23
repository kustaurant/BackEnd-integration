package com.kustaurant.mainapp.common.discordAlert;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertRateLimiter {
    private static final long WINDOW_MS = 300_000; //5분
    private final ConcurrentHashMap<String, Long> lastSent = new ConcurrentHashMap<>();

    public boolean allow(String key) {
        final long now = System.currentTimeMillis();
        final boolean[] allowed = {false};

        lastSent.compute(key, (k, prev) -> {
            if (prev == null || now - prev > WINDOW_MS) {
                allowed[0] = true;
                return now;
            } else {
                allowed[0] = false;
                return prev;
            }
        });
        return allowed[0];
    }
}
