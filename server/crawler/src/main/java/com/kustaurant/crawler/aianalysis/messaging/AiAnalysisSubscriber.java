package com.kustaurant.crawler.aianalysis.messaging;

import com.kustaurant.crawler.aianalysis.service.AiAnalysisOrchestrator;
import com.kustaurant.crawler.aianalysis.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.infrastructure.messaging.MessageSubscriber;
import com.kustaurant.crawler.infrastructure.messaging.redis.RedisStreamsReclaimer;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
public class AiAnalysisSubscriber {

    private final MessagingProps props;
    private final MessageSubscriber subscriber;
    private final AiAnalysisOrchestrator orchestrator;

    private final StringRedisTemplate redisTemplate;

    private final String consumerName = UUID.randomUUID().toString();
    private static final ReentrantReadWriteLock rw = new ReentrantReadWriteLock(true);

    public static Lock readLock() {
        return rw.readLock();
    }

    public static Lock writeLock() {
        return rw.writeLock();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        subscriber.subscribe(
                props.aiAnalysisStart(),
                props.group(),
                consumerName,
                AiAnalysisRequest.class,
                orchestrator::execute
        );
    }

    @Bean
    public RedisStreamsReclaimer<?> redisStreamsReclaimer() {
        return new RedisStreamsReclaimer<>(
                redisTemplate,
                AiAnalysisRequest.class,
                orchestrator::execute,
                props.aiAnalysisStart(),
                props.group(),
                consumerName,
                props.aiAnalysisDlq()
        );
    }
}
