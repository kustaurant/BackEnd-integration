package com.kustaurant.crawler.IGpartnership.dto;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;

public record CrawlRequest(
        String accountName,
        PartnershipTarget target
) {}
