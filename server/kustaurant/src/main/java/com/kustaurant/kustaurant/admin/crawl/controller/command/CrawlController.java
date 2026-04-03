package com.kustaurant.kustaurant.admin.crawl.controller.command;

import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipPipelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/crawl")
public class CrawlController {
    private final IGPartnershipPipelineService pipelineService;

    // 1. 크롤링후 매칭까지 한번에 진행
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/ig/run")
    public IgCrawlResponse runPipeline(@Valid @RequestBody IgCrawlRequest req) {
        return pipelineService.crawlAndImport(
                req.accountName(),
                req.target()
        );
    }
}
