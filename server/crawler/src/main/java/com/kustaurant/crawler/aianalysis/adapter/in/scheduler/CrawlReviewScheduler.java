package com.kustaurant.crawler.aianalysis.adapter.in.scheduler;

import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessagingProps;
import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.CrawlingReviewReq;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.Message;
import com.kustaurant.crawler.aianalysis.adapter.out.messaging.MessageReader;
import com.kustaurant.crawler.aianalysis.service.port.CrawlReviewService;
import com.kustaurant.crawler.global.util.SystemMemoryUtils;
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
public class CrawlReviewScheduler {

    private static final int MAX_CONCURRENT_JOBS = 5;
    private static final double MAX_MEMORY_USAGE = 0.7;
    private static final long MIN_AVAILABLE_MEMORY_BYTES = 300_000_000L;

    private static final ExecutorService WORKER_POOL = new ThreadPoolExecutor(
            MAX_CONCURRENT_JOBS, MAX_CONCURRENT_JOBS,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    private final CrawlReviewService crawlReviewService;
    private final MessageReader messageReader;
    private final MessagingProps props;

    private final String consumerName = UUID.randomUUID().toString();

    @Scheduled(fixedRate = 5_000)
    public void execute() {
        if (!canStartNextJob()) return;

        WORKER_POOL.submit(this::processOneMessage);
    }

    private void processOneMessage() {
        Optional<Message<CrawlingReviewReq>> msgOptional = messageReader.read(
                props.aiAnalysisCrawling(),
                props.group(),
                consumerName,
                CrawlingReviewReq.class
        );

        if (msgOptional.isEmpty()) {
            return;
        }

        Message<CrawlingReviewReq> msg = msgOptional.get();
        try {
            crawlReviewService.crawlReviews(msg.payload());
        } finally {
            msg.doAck().run();
        }
    }

    private boolean canStartNextJob() {
        double usage = SystemMemoryUtils.getSystemMemoryUsage();
        long available = SystemMemoryUtils.getAvailableMemory();

        return usage <= MAX_MEMORY_USAGE || available >= MIN_AVAILABLE_MEMORY_BYTES;
    }
}
