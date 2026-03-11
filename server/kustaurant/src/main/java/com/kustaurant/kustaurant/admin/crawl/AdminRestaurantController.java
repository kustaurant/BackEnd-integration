package com.kustaurant.kustaurant.admin.crawl;

import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
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

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping
    public PagedPartnershipResponse  getPartnerships(
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

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public void updatePartnership(
            @PathVariable Long id,
            @RequestBody AdminPartnershipUpdateRequest req
    ) {
        service.updatePartnership(id, req);
    }
}
