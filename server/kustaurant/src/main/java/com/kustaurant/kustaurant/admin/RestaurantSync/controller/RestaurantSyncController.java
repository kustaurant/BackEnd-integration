package com.kustaurant.kustaurant.admin.RestaurantSync.controller;

import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateActionResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateApproveRequest;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncRunRequest;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncRunResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.service.RestaurantSyncService;
import io.swagger.v3.oas.annotations.Hidden;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/sync")
public class RestaurantSyncController {

    private final RestaurantSyncService restaurantSyncService;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/candidates")
    public RestaurantSyncRunResponse runSync(@RequestBody(required = false) RestaurantSyncRunRequest request) {
        return restaurantSyncService.generateCandidatesAndSync(request == null ? null : request.crawlScope());
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/candidates")
    public List<RestaurantSyncCandidateResponse> getCandidates(
            @RequestParam(defaultValue = "PENDING") SyncCandidateStatus status
    ) {
        return restaurantSyncService.getCandidates(status);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/candidates/{candidateId}/approve")
    public RestaurantSyncCandidateActionResponse approve(
            @PathVariable Long candidateId,
            @RequestBody(required = false) RestaurantSyncCandidateApproveRequest request,
            Principal principal
    ) {
        return restaurantSyncService.approve(
                candidateId,
                principal == null ? "UNKNOWN_ADMIN" : principal.getName(),
                request == null ? null : request.manualCuisine()
        );
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/candidates/{candidateId}/reject")
    public RestaurantSyncCandidateActionResponse reject(
            @PathVariable Long candidateId,
            Principal principal
    ) {
        return restaurantSyncService.reject(candidateId, principal == null ? "UNKNOWN_ADMIN" : principal.getName());
    }
}
