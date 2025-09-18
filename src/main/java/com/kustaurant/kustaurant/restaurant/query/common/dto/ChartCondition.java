package com.kustaurant.kustaurant.restaurant.query.common.dto;

import java.util.List;
import org.springframework.data.domain.Pageable;

public record ChartCondition(
        List<String> cuisines,
        List<Long> situations,
        List<String> positions,
        TierFilter tierFilter,
        Pageable pageable
) {

    public ChartCondition changeTierFilter(TierFilter newFilter) {
        return new ChartCondition(
                cuisines, situations, positions, newFilter, pageable
        );
    }

    public boolean needAll() {
        return tierFilter == TierFilter.ALL;
    }

    public boolean needOnlyTier() {
        return tierFilter == TierFilter.WITH_TIER;
    }

    public enum TierFilter {
        WITH_TIER,
        WITHOUT_TIER,
        ALL
    }
}
