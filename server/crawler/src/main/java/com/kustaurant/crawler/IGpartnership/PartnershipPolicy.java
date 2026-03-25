package com.kustaurant.crawler.IGpartnership;

import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;

public interface PartnershipPolicy {
    boolean supports(PartnershipTarget target);

    boolean isValid(CaptionParser.Parsed parsed);
}
