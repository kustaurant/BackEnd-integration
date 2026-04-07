package com.kustaurant.kustaurant.admin.crawl.controller.command;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;

@Hidden
public record IgCrawlRequest(
        @NotNull String accountName,
        @NotNull PartnershipTarget target
) {}
