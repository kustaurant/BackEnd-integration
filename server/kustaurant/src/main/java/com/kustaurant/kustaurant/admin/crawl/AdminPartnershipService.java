package com.kustaurant.kustaurant.admin.crawl;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantPartnershipJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminPartnershipService {
    private final AdminPartnershipQueryRepository repo;
    private final RestaurantPartnershipJpaRepository jpaRepo;

    public PagedPartnershipResponse getPartnerships(
            PartnershipTarget target,
            MatchStatus matchStatus,
            String sourceAccount,
            String keyword,
            Pageable pageable
    ) {
        Page<RestaurantPartnershipEntity> page = repo.searchPartnerships(
                target, matchStatus, sourceAccount, keyword, pageable
        );

        List<PartnershipListResponse> partnerships = page.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PagedPartnershipResponse(
                partnerships,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    private PartnershipListResponse toResponse(RestaurantPartnershipEntity entity) {
        return new PartnershipListResponse(
                entity.getId(),
                entity.getRestaurantId(),
                entity.getRestaurantName(),
                entity.getTarget().name(),
                entity.getBenefit(),
                entity.getMatchStatus().name(),
                entity.getPostUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Transactional
    public void updatePartnership(Long id, AdminPartnershipUpdateRequest req) {

        RestaurantPartnershipEntity entity = jpaRepo.findById(id).orElseThrow();

        if (req.restaurantId() != null) entity.setRestaurantId(req.restaurantId());
        if (req.restaurantName() != null) entity.setRestaurantName(req.restaurantName());
        if (req.benefit() != null) entity.setBenefit(req.benefit());
        if (req.locationText() != null) entity.setLocationText(req.locationText());
        if (req.contactPhone() != null) entity.setContactPhone(req.contactPhone());
        if (req.matchStatus() != null) entity.setMatchStatus(req.matchStatus());
    }
}
