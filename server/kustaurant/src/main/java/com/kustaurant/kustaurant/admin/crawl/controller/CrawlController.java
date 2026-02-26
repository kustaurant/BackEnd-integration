package com.kustaurant.kustaurant.admin.crawl.controller;

import com.kustaurant.kustaurant.admin.crawl.service.IGPartnershipImportService;
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

    // 크롤링 해서 결과를 저장 (계정명을 기준으로 하여 매번 초기화 후 다시 저장)
    @PostMapping("/ig/raw")
    public int saveRaw(@Valid @RequestBody IgCrawlRequest req) {
        return saveService.crawlAndReplaceAll(req.accountName(), req.target());
    }

    @PostMapping("/ig/import")
    public int importPartnership (@Valid @RequestBody IgCrawlRequest req) {
        int saved = ImportService.importFromIG(req.accountName(), req.target());
        return saved;
    }
}
