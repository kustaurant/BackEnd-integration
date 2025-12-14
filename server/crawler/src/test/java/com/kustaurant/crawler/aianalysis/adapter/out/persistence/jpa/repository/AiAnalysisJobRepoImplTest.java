package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AiAnalysisJobRepoImplTest {

    @Autowired
    private AiAnalysisJobRepoImpl aiAnalysisJobRepo;

    @Test
    void 벌크_쿼리_동시_데드락_테스트() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(100);

        long restaurantId = 1;
        LocalDateTime now = LocalDateTime.now(
                Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC")));

        long jobId = aiAnalysisJobRepo.save(AiAnalysisJob.create(restaurantId, now));

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(pool.submit(() -> aiAnalysisJobRepo.updateReviewCount(jobId, true, now)));
        }

        // 1) 작업 완료 대기 + 예외 전파
        for (Future<?> f : futures) f.get();

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println(aiAnalysisJobRepo.findJob(jobId).getProcessedReviews());
    }
}