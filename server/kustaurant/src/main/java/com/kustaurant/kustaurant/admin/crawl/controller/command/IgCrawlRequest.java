package com.kustaurant.kustaurant.admin.crawl.controller.command;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import jakarta.validation.constraints.NotNull;

public record IgCrawlRequest(
        @NotNull String accountName,
        @NotNull PartnershipTarget target
) {}
