package com.kustaurant.kustaurant.admin.IGCrawl.service.queryTop3;

import com.kustaurant.kustaurant.admin.IGCrawl.dto.RestaurantMatchCandidate;
import com.kustaurant.kustaurant.admin.IGCrawl.service.matching.AddressNormalizer;
import com.kustaurant.kustaurant.admin.IGCrawl.service.matching.RestaurantNameNormalizer;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantCandidateRepository;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory.InMemorySearchEngineManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RestaurantTopCandidateFinder {
    private final InMemorySearchEngineManager searchEngineManager;
    private final RestaurantNameNormalizer restaurantNameNormalizer;
    private final AddressNormalizer addressNormalizer;
    private final RestaurantCandidateRepository restaurantCandidateRepository;

    public List<RestaurantMatchCandidate> findTop3(String rawRestaurantName, String rawLocationText) {
        String normalizedQuery = restaurantNameNormalizer.normalize(rawRestaurantName);
        if (normalizedQuery.isEmpty()) {
            return List.of();
        }

        InMemorySearchEngineManager.Snapshot snapshot = searchEngineManager.snapshot();

        Set<Long> candidateIds = new LinkedHashSet<>();

        // 1. 이름 전체로 먼저
        collect(snapshot, normalizedQuery, candidateIds);

        // 2. 그래도 부족하면 부분 토큰으로 확장
        if (candidateIds.size() < 3) {
            for (String token : buildSearchTokens(normalizedQuery)) {
                collect(snapshot, token, candidateIds);
                if (candidateIds.size() >= 20) break;
            }
        }

        if (candidateIds.isEmpty()) return List.of();

        List<RestaurantMatchCandidate> candidates = restaurantCandidateRepository.findMatchCandidatesByIds(candidateIds);

        return candidates.stream()
                .sorted((a, b) -> Integer.compare(
                        score(rawRestaurantName, rawLocationText, b),
                        score(rawRestaurantName, rawLocationText, a)
                ))
                .limit(3)
                .toList();
    }

    private void collect(InMemorySearchEngineManager.Snapshot snapshot,
                         String token,
                         Set<Long> candidateIds) {
        long[] postings = snapshot.titlePostings(token);
        if (postings == null) return;

        for (long id : postings) candidateIds.add(id);
    }

    private List<String> buildSearchTokens(String normalizedQuery) {
        Set<String> tokens = new LinkedHashSet<>();
        int len = normalizedQuery.length();

        for (int gram = len; gram >= 2; gram--) {
            for (int i = 0; i <= len - gram; i++) {
                tokens.add(normalizedQuery.substring(i, i + gram));
            }
        }

        return new ArrayList<>(tokens);
    }

    private int score(String rawRestaurantName, String rawLocationText, RestaurantMatchCandidate candidate) {
        int score = 0;

        String queryName = restaurantNameNormalizer.normalize(rawRestaurantName);
        String candidateName = restaurantNameNormalizer.normalize(candidate.name());

        if (queryName.equals(candidateName)) score += 1000;
        else if (candidateName.contains(queryName) || queryName.contains(candidateName)) score += 500;

        if (addressNormalizer.isCompatible(rawLocationText, candidate.address())) score += 100;

        return score;
    }
}
