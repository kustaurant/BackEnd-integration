package com.kustaurant.crawler.aianalysis.adapter.in.scheduler;

import com.kustaurant.crawler.aianalysis.service.port.CreateJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateJobScheduler {

    private final CreateJobService createJobService;

    @EventListener(ApplicationReadyEvent.class)
    public void ratingInit() {
        createJob();
    }

    // 매일 새벽 4시 리뷰 크롤링 Job 생성
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void createJob() {
        int created = createJobService.createJobs();
        log.info("review crawling jobs created: {}", created);
    }
}
