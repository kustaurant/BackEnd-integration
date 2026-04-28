package com.kustaurant.kustaurant.admin.IGCrawl.controller.command;

import com.kustaurant.restaurant.enums.PartnershipTarget;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;

@Hidden
public record IgCrawlRequest(
        @NotNull String accountName,
        @NotNull PartnershipTarget target
) {}
