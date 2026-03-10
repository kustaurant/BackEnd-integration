package com.kustaurant.crawler.aianalysis.adapter.in.controller;

import com.kustaurant.crawler.aianalysis.service.port.CreateJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateJobController {

    private final CreateJobService createJobService;

    @PostMapping("/admin/api/review-analysis")
    public void createJob() {
        int created = createJobService.createJobs();
        log.info("review crawling jobs created: {}", created);
    }
}
