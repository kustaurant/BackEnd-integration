package com.kustaurant.kustaurant.admin.IGCrawl.service.matching;

import com.kustaurant.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.restaurant.enums.MatchStatus;
import com.kustaurant.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.PartnershipUpdateRequest;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.query.PagedPartnershipResponse;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.query.PartnershipListResponse;
import com.kustaurant.kustaurant.admin.IGCrawl.infrastructure.AdminPartnershipQueryRepository;
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
    private final AdminPartnershipQueryRepository queryRepo;
    private final RestaurantPartnershipJpaRepository jpaRepo;

    // 전체 조회
    public PagedPartnershipResponse getPartnerships(
            PartnershipTarget target,
            MatchStatus matchStatus,
            String sourceAccount,
            String keyword,
            Pageable pageable
    ) {
        Page<RestaurantPartnershipEntity> page = queryRepo.searchPartnerships(
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

    // 단건 조회
    public PartnershipListResponse getPartnership(Long id) {
        RestaurantPartnershipEntity entity = jpaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 제휴 정보를 찾을 수 없습니다. id=" + id));

        return toResponse(entity);
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

    // 단건 업데이트
    @Transactional
    public void updatePartnership(Long id, PartnershipUpdateRequest req) {
        RestaurantPartnershipEntity entity = jpaRepo.findById(id).orElseThrow();

        if (req.restaurantId() != null) entity.setRestaurantId(req.restaurantId());
        else entity.setRestaurantId(null);

        if (req.restaurantName() != null) entity.setRestaurantName(req.restaurantName());
        if (req.benefit() != null) entity.setBenefit(req.benefit());
        if (req.locationText() != null) entity.setLocationText(req.locationText());

        entity.setMatchStatus(entity.getRestaurantId() == null ? MatchStatus.UNMATCHED : MatchStatus.MATCHED);
    }

    // 데이터 삭제
    @Transactional
    public long deletePartnerships(String target) {
        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("삭제 대상 target이 비어있습니다.");
        }

        if ("ALLDATA".equals(target)) {
            long count = jpaRepo.count();
            jpaRepo.deleteAllInBatch();
            return count;
        }

        PartnershipTarget partnershipTarget;
        try {
            partnershipTarget = PartnershipTarget.valueOf(target);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 target 값입니다: " + target);
        }

        long count = jpaRepo.countByTarget(partnershipTarget);
        jpaRepo.deleteAllByTarget(partnershipTarget);
        return count;
    }
}
