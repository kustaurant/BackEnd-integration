package com.kustaurant.kustaurant.admin.IGCrawl.service.matching;

import com.kustaurant.kustaurant.admin.IGCrawl.dto.NameMatchDecision;
import com.kustaurant.kustaurant.admin.IGCrawl.dto.RestaurantMatchCandidate;
import com.kustaurant.kustaurant.admin.IGCrawl.infrastructure.IgCrawlRawEntity;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantCandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantNameMatchService {

    private final RestaurantNameExactCandidateFinder candidateFinder;
    private final RestaurantCandidateRepository candidateRepo;
    private final AddressNormalizer addressNormalizer;

    public NameMatchDecision match(IgCrawlRawEntity raw) {
        String rawRestaurantName = raw.getRestaurantName();
        if (rawRestaurantName == null || rawRestaurantName.isBlank()) {
            return NameMatchDecision.unmatched("raw restaurantName 없음");
        }

        List<Long> candidateIds = candidateFinder.findCandidates(rawRestaurantName);
        if (candidateIds.isEmpty()) return NameMatchDecision.unmatched("이름 완전일치 후보 없음");

        List<RestaurantMatchCandidate> candidates = candidateRepo.findMatchCandidatesByIds(candidateIds);
        if (candidates.isEmpty()) return NameMatchDecision.unmatched("후보 상세 조회 결과 없음");

        List<RestaurantMatchCandidate> addressMatched = candidates.stream()
                .filter(candidate -> addressNormalizer.isCompatible(raw.getLocation(), candidate.address()))
                .toList();

        if (addressMatched.size() == 1) {
            return NameMatchDecision.matched(
                    addressMatched.get(0).id(),
                    "이름 완전일치 + 주소 일치"
            );
        }

        if (candidates.size() == 1 && addressMatched.isEmpty()) {
            return NameMatchDecision.unmatched("이름 완전일치 후보 1개지만 주소 불일치");
        }

        if (addressMatched.isEmpty()) {
            return NameMatchDecision.unmatched("이름 완전일치 후보는 있으나 주소 일치 후보 없음");
        }

        return NameMatchDecision.unmatched("주소 일치 후보가 여러 개라 자동매칭 보류");
    }
}
