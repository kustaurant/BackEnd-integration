package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.crawl.controller.command.IgImportResult;
import com.kustaurant.kustaurant.admin.crawl.controller.command.IgRawSaveResult;
import com.kustaurant.kustaurant.admin.crawl.controller.command.IgCrawlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IGPartnershipPipelineService {

    private final IgCrawlRawSaveService rawSaveService;
    private final IGPartnershipImportService importService;

    @Transactional
    public IgCrawlResponse crawlAndImport(String accountName, PartnershipTarget target) {
        log.info("22");
        IgRawSaveResult rawResult = rawSaveService.crawlAndReplaceAll(accountName, target);
        if (rawResult.rawSavedCount() == 0) return new IgCrawlResponse(rawResult.crawledPages(), 0, 0, 0);
        log.info("33");

        IgImportResult importResult = importService.importFromRaw(accountName, target);
        return new IgCrawlResponse(
                rawResult.crawledPages(),
                rawResult.rawSavedCount(),
                importResult.matchedRestaurantCount(),
                importResult.unmatchedRestaurantCount()
        );
    }
}
