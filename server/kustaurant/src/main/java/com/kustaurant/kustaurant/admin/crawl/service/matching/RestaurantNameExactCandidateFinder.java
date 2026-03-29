package com.kustaurant.kustaurant.admin.crawl.service.matching;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory.InMemorySearchEngineManager;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory.InMemorySearchTextProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RestaurantNameExactCandidateFinder {

    private final InMemorySearchEngineManager searchEngineManager;
    private final RestaurantNameNormalizer restaurantNameNormalizer;

    public List<Long> findCandidates(String rawRestaurantName) {
        String normalizedQuery = restaurantNameNormalizer.normalize(rawRestaurantName);
        if (normalizedQuery.isEmpty()) return List.of();

        InMemorySearchEngineManager.Snapshot snapshot = searchEngineManager.snapshot();
        List<String> tokens = InMemorySearchTextProcessor.tokenizeQuery(rawRestaurantName);

        Set<Long> candidateSet = new LinkedHashSet<>();

        for (String token : tokens) {
            long[] postings = snapshot.titlePostings(token);
            if (postings == null) continue;

            for (long id : postings) {
                candidateSet.add(id);
            }
        }

        if (candidateSet.isEmpty()) return List.of();

        List<Long> exactMatchedIds = new ArrayList<>();

        for (long restaurantId : candidateSet) {
            String title = snapshot.title(restaurantId);
            String normalizedTitle = restaurantNameNormalizer.normalize(title);

            if (normalizedQuery.equals(normalizedTitle)) exactMatchedIds.add(restaurantId);
        }

        return exactMatchedIds;
    }
}