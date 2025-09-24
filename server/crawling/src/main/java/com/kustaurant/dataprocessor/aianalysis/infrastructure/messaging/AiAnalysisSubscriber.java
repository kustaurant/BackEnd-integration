package com.kustaurant.dataprocessor.aianalysis.infrastructure.messaging;

import com.kustaurant.dataprocessor.aianalysis.AiAnalysisOrchestrator;
import com.kustaurant.dataprocessor.aianalysis.infrastructure.messaging.dto.AiAnalysisRequest;
import com.kustaurant.dataprocessor.infrastructure.messaging.MessageSubscriber;
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
