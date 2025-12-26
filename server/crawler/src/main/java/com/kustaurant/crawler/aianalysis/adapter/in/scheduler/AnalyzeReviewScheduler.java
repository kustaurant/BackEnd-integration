package com.kustaurant.crawler.aianalysis.adapter.in.scheduler;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;
import com.kustaurant.crawler.aianalysis.adapter.out.ai.openai.OpenAiRateLimitGate;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.Message;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessageReader;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.service.port.AnalyzeReviewService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzeReviewScheduler {

    private static final int MAX_CONCURRENT_JOBS = 20;

    private static final ExecutorService WORKER_POOL = new ThreadPoolExecutor(
            MAX_CONCURRENT_JOBS, MAX_CONCURRENT_JOBS,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    private final MessageReader messageReader;
    private final MessagingProps props;
    private final AnalyzeReviewService analyzeReviewService;
    private final OpenAiRateLimitGate rateLimitGate;

    private final String consumerName = UUID.randomUUID().toString();

    @Scheduled(fixedRate = 100)
    public void execute() {
        if (!rateLimitGate.tryEnter()) return;

        WORKER_POOL.submit(this::processOneMessage);
    }

    private void processOneMessage() {
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
        } catch (Exception e) {
            log.error("[AnalyzeReviewScheduler] analyzeAndSave failed", e);
        } finally {
            msg.doAck().run();
        }
    }
}
