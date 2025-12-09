package com.kustaurant.crawler.aianalysis.adapter.in.scheduler;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.Message;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessageReader;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.service.port.AnalyzeReviewService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyzeReviewScheduler {

    private final MessageReader messageReader;
    private final MessagingProps props;
    private final AnalyzeReviewService analyzeReviewService;

    private final String consumerName = UUID.randomUUID().toString();

    // TODO: 이제 OpenAI 정책 & 429 응답에 맞춰서 호출 속도 조정
    @Scheduled(fixedRate = 2_000)
    public void execute() {
        Optional<Message<AiAnalysisReq>> msgOptional = messageReader.read(
                props.aiAnalysisReview(),
                props.group(),
                consumerName,
                AiAnalysisReq.class
        );

        if (msgOptional.isEmpty()) {
            return;
        }

        Message<AiAnalysisReq> msg = msgOptional.get();
        try {
            analyzeReviewService.analyzeAndSave(msg.payload());
        } finally {
            msg.doAck().run();
        }
    }
}
