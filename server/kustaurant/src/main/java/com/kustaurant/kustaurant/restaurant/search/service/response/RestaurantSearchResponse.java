package com.kustaurant.kustaurant.restaurant.search.service.response;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record RestaurantSearchResponse(
        List<RestaurantSearchItem> items,
        boolean hasNext
) {
    public RestaurantSearchResponse(SearchResult searchResult, Map<Long, RestaurantForSearch> restaurantInfos) {
        this(new ArrayList<>(), searchResult.ids.hasNext());
        for (Long id : searchResult.ids.getContent()) {
            RestaurantForSearch restaurant = restaurantInfos.get(id);
            if (restaurant == null) {
                continue;
            }

            SearchResult.MatchInfo matchInfo = searchResult.matchInfo.get(id);

            List<String> matchedMenus = matchInfo == null || matchInfo.matchedMenus == null
                    ? List.of()
                    : matchInfo.matchedMenus;

            List<RestaurantSearchItem.HighlightRange> titleHighlights = matchInfo == null
                    || matchInfo.titleHighlights == null
                    ? List.of()
                    : matchInfo.titleHighlights.stream()
                            .map(range -> new RestaurantSearchItem.HighlightRange(range.start, range.end))
                            .toList();

            List<RestaurantSearchItem.HighlightRange> categoryHighlights = matchInfo == null
                    || matchInfo.categoryHighlights == null
                    ? List.of()
                    : matchInfo.categoryHighlights.stream()
                            .map(range -> new RestaurantSearchItem.HighlightRange(range.start, range.end))
                            .toList();

            List<String> matchedFields = matchInfo == null || matchInfo.fields == null
                    ? List.of()
                    : matchInfo.fields.stream()
                            .map(field -> field.name().toLowerCase(java.util.Locale.ROOT))
                            .toList();

            items.add(new RestaurantSearchItem(
                    restaurant.name(),
                    restaurant.cuisine(),
                    restaurant.position(),
                    restaurant.imgUrl(),
                    restaurant.tier(),
                    restaurant.partnershipInfo(),
                    restaurant.isEvaluated(),
                    restaurant.isFavorite(),
                    matchedMenus,
                    titleHighlights,
                    categoryHighlights,
                    matchedFields
            ));
        }
    }

    public record RestaurantSearchItem(
            String name,
            String cuisine,
            String position,
            String imgUrl,
            int tier,
            String partnershipInfo,
            boolean isEvaluated,
            boolean isFavorite,

            List<String> matchedMenus,
            List<HighlightRange> titleHighlights,
            List<HighlightRange> categoryHighlights,
            List<String> matchedFields
    ) {

        public record HighlightRange(
                int start,
                int end
        ) {
        }
    }
}
