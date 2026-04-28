package com.kustaurant.kustaurant.admin.IGCrawl.controller.query;

import com.kustaurant.restaurant.enums.MatchStatus;
import com.kustaurant.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.PartnershipDeleteRequest;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.PartnershipDeleteResponse;
import com.kustaurant.kustaurant.admin.IGCrawl.service.matching.AdminPartnershipService;
import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.PartnershipUpdateRequest;
import com.kustaurant.kustaurant.admin.IGCrawl.dto.PartnershipCandidateResponse;
import com.kustaurant.kustaurant.admin.IGCrawl.service.queryTop3.PartnershipCandidateQueryService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/partnerships")
public class AdminRestaurantController {

    private final AdminPartnershipService service;
    private final PartnershipCandidateQueryService queryService;

    //1. 제휴 음식점 조회
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping
    public PagedPartnershipResponse getPartnerships(
            @RequestParam(required = false) PartnershipTarget target,
            @RequestParam(required = false) MatchStatus matchStatus,
            @RequestParam(required = false) String sourceAccount,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.getPartnerships(
                target,
                matchStatus,
                sourceAccount,
                keyword,
                pageable
        );
    }

    //2. 단일 제휴 정보 조회
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public PartnershipListResponse getPartnership(@PathVariable Long id) {
        return service.getPartnership(id);
    }

    //3. 제휴 정보 수정
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public void updatePartnership(
            @PathVariable Long id,
            @RequestBody PartnershipUpdateRequest req
    ) {
        service.updatePartnership(id, req);
    }

    //4. partnership 데이터 중 유사 top3 식당 조회
    @GetMapping("/{partnershipId}/candidates")
    public PartnershipCandidateResponse getCandidates(@PathVariable Long partnershipId) {
        return queryService.getCandidates(partnershipId);
    }

    //5. 제휴 데이터 삭제
    @DeleteMapping
    public PartnershipDeleteResponse deletePartnerships(@RequestBody PartnershipDeleteRequest request) {
        long deletedCount = service.deletePartnerships(request.target());
        return new PartnershipDeleteResponse(deletedCount);
    }
}
