package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IGPartnershipPipelineService {

    private final IgCrawlRawSaveService rawSaveService;
    private final IGPartnershipImportService importService;

    @Transactional
    public int crawlAndImport(String accountName, PartnershipTarget target) {
        int rawCount = rawSaveService.crawlAndReplaceAll(accountName, target);
        if (rawCount == 0) return 0;

        return importService.importFromRaw(accountName, target);
    }
}
