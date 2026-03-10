package com.kustaurant.crawler.aianalysis.adapter.out.ai.openai;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class OpenAiRateLimitGate {
    private final AtomicLong blockedUntilMs = new AtomicLong(0);

    public boolean tryEnter() {
        return System.currentTimeMillis() >= blockedUntilMs.get();
    }

    public void blockForMs(long ms) {
        long until = System.currentTimeMillis() + ms;
        blockedUntilMs.accumulateAndGet(until, Math::max);
    }
}
