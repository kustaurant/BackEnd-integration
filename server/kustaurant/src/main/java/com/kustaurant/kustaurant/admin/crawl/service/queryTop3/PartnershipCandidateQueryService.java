package com.kustaurant.kustaurant.admin.crawl.service.queryTop3;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.kustaurant.admin.crawl.dto.PartnershipCandidateResponse;
import com.kustaurant.kustaurant.admin.crawl.dto.RestaurantMatchCandidate;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantPartnershipJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnershipCandidateQueryService {

    private final RestaurantPartnershipJpaRepository partnershipRepository;
    private final RestaurantTopCandidateFinder restaurantTopCandidateFinder;

    @Transactional(readOnly = true)
    public PartnershipCandidateResponse getCandidates(Long partnershipId) {
        RestaurantPartnershipEntity partnership = partnershipRepository.findById(partnershipId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 partnershipId: " + partnershipId));

        List<RestaurantMatchCandidate> candidates = restaurantTopCandidateFinder.findTop3(
                partnership.getRestaurantName(),
                partnership.getLocationText()
        );

        List<PartnershipCandidateResponse.CandidateItem> items = candidates.stream()
                .map(candidate -> new PartnershipCandidateResponse.CandidateItem(
                        candidate.id(),
                        candidate.name(),
                        candidate.address(),
                        candidate.phoneNumber()
                ))
                .toList();

        return new PartnershipCandidateResponse(
                partnership.getId(),
                partnership.getRestaurantName(),
                partnership.getLocationText(),
                partnership.getBenefit(),
                items
        );
    }
}