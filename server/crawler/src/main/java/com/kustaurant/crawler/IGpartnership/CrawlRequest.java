package com.kustaurant.crawler.IGpartnership;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;

public record CrawlRequest(
        String accountName,
        PartnershipTarget target
) {}
