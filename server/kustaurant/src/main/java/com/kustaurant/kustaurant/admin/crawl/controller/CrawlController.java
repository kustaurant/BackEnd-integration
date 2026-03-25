package com.kustaurant.kustaurant.admin.crawl.controller;

import com.kustaurant.kustaurant.admin.crawl.IgImportResult;
import com.kustaurant.kustaurant.admin.crawl.IgRawSaveResult;
import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipImportService;
import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipPipelineService;
import com.kustaurant.kustaurant.admin.crawl.service.IgCrawlRawSaveService;
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
    private final IgCrawlRawSaveService saveService;
    private final  IGPartnershipImportService importService;

    private final IGPartnershipPipelineService pipelineService;

    // 크롤링 해서 결과를 저장 (계정명을 기준으로 하여 매번 초기화 후 다시 저장)
    @PostMapping("/ig/raw")
    public IgRawSaveResult saveRaw(@Valid @RequestBody IgCrawlRequest req) {
        return saveService.crawlAndReplaceAll(req.accountName(), req.target());
    }

    // 크롤링한 raw 데이터 매칭
    @PostMapping("/ig/import")
    public IgImportResult importPartnership (@Valid @RequestBody IgCrawlRequest req) {
        return importService.importFromRaw(req.accountName(), req.target());
    }

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
