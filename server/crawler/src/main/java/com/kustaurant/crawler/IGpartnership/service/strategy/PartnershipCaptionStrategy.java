package com.kustaurant.crawler.IGpartnership.service.strategy;

import com.kustaurant.crawler.IGpartnership.dto.ParsedCaption;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;

public interface PartnershipCaptionStrategy {
    boolean supports(PartnershipTarget target);

    ParsedCaption parse(String caption);

    boolean hasRequiredFields(ParsedCaption parsedCaption);
}
