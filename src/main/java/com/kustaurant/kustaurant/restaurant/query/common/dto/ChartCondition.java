package com.kustaurant.kustaurant.restaurant.query.common.dto;

import java.util.List;

public record ChartCondition(
        List<String> cuisines,
        List<Long> situations,
        List<String> positions,
        TierFilter tierFilter
) {

    public ChartCondition changeTierFilter(TierFilter newFilter) {
        return new ChartCondition(
                cuisines, situations, positions, newFilter
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
