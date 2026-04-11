package com.kustaurant.kustaurant.admin.IGCrawl.controller.command;

import com.kustaurant.kustaurant.admin.IGCrawl.service.IGPartnershipPipelineService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Hidden
@RequestMapping("/admin/api/crawl")
public class CrawlController {

    private final IGPartnershipPipelineService pipelineService;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/ig/run")
    public IgCrawlResponse runPipeline(@Valid @RequestBody IgCrawlRequest req) {
        return pipelineService.crawlAndImport(
                req.accountName(),
                req.target()
        );
    }
}
