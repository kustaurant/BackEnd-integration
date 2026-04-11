package com.kustaurant.kustaurant.admin.IGCrawl.service;

import com.kustaurant.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.IgImportResult;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.IgRawSaveResult;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.IgCrawlResponse;
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
        IgRawSaveResult rawResult = rawSaveService.crawlAndReplaceAll(accountName, target);
        if (rawResult.rawSavedCount() == 0) {
            return new IgCrawlResponse(rawResult.crawledPages(), 0, 0, 0);
        }

        IgImportResult importResult = importService.importFromRaw(accountName, target);
        return new IgCrawlResponse(
                rawResult.crawledPages(),
                rawResult.rawSavedCount(),
                importResult.matchedRestaurantCount(),
                importResult.unmatchedRestaurantCount()
        );
    }
}
