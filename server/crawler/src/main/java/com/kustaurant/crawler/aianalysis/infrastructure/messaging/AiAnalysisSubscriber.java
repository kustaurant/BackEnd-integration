package com.kustaurant.crawler.aianalysis.infrastructure.messaging;

import com.kustaurant.crawler.aianalysis.service.AiAnalysisOrchestrator;
import com.kustaurant.crawler.aianalysis.infrastructure.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.infrastructure.messaging.MessageSubscriber;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiAnalysisSubscriber {

    private final MessagingProps props;
    private final MessageSubscriber subscriber;
    private final AiAnalysisOrchestrator orchestrator;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        subscriber.subscribe(
                props.aiAnalysisStart(),
                props.group(),
                UUID.randomUUID().toString(),
                AiAnalysisRequest.class,
                orchestrator::execute
        );
    }
}
