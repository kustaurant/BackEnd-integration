package com.kustaurant.kustaurant.restaurant.query.common.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

public record ChartCondition(
        List<String> cuisines,
        List<Long> situations,
        List<String> positions,
        TierFilter tierFilter,
        Pageable pageable
) {
    // 캐시 키 추가를 위한 기본1개 + 덕지덕지 로직2개
    public String cacheKey() {
        return String.join("|",
                "tier=" + tierFilter,
                "cuisines=" + norm(cuisines),
                "situations=" + norm(situations),
                "positions=" + norm(positions),
                "page=" + pageable.getPageNumber(),
                "size=" + pageable.getPageSize(),
                "sort=" + normSort(pageable.getSort())
        );
    }
    private static String norm(List<?> list) {
        if (list == null || list.isEmpty()) return "*";
        return list.stream()
                .map(String::valueOf)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static String normSort(org.springframework.data.domain.Sort sort) {
        if (sort == null || sort.isUnsorted()) return "unsorted";
        return sort.stream()
                .map(o -> o.getProperty() + ":" + o.getDirection().name())
                .sorted()
                .collect(Collectors.joining(","));
    }
    // -------------------

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
