package com.kustaurant.kustaurant.admin.crawl.controller;

import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipImportService;
import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipPipelineService;
import com.kustaurant.kustaurant.admin.crawl.service.IgCrawlRawSaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/crawl")
public class CrawlController {
    private final IgCrawlRawSaveService saveService;
    private final IGPartnershipImportService ImportService;
    private final IGPartnershipPipelineService pipelineService;

    // 1. 크롤링 해서 결과를 저장 (계정명을 기준으로 하여 매번 초기화 후 다시 저장)
    @PostMapping("/ig/raw")
    public int saveRaw(@Valid @RequestBody IgCrawlRequest req) {
        return saveService.crawlAndReplaceAll(req.accountName(), req.target());
    }

    // 2. 크롤링한 raw 데이터 매칭
    @PostMapping("/ig/import")
    public int importPartnership (@Valid @RequestBody IgCrawlRequest req) {
        int saved = ImportService.importFromRaw(req.accountName(), req.target());
        return saved;
    }

    // 3. 크롤링후 매칭까지 한번에 진행
    @PostMapping("/ig/run")
    public int runPipeline(@Valid @RequestBody IgCrawlRequest req) {

        return pipelineService.crawlAndImport(
                req.accountName(),
                req.target()
        );
    }
}
