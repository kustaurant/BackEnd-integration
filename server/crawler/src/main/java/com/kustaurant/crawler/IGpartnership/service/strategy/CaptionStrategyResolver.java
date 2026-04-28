package com.kustaurant.crawler.IGpartnership.service.strategy;

import com.kustaurant.restaurant.enums.PartnershipTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CaptionStrategyResolver {
    private final List<PartnershipCaptionStrategy> strategies;

    public PartnershipCaptionStrategy resolve(PartnershipTarget target) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(target))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "지원하는 caption strategy가 없습니다. target=" + target
                ));
    }
}
