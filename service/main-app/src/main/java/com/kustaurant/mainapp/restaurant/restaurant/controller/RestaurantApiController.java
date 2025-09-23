package com.kustaurant.mainapp.restaurant.restaurant.controller;

import com.kustaurant.mainapp.common.view.ViewCountService;
import com.kustaurant.mainapp.common.view.ViewResourceType;
import com.kustaurant.mainapp.common.view.ViewerKeyProvider;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.restaurant.restaurant.service.RestaurantQueryService;
import com.kustaurant.mainapp.restaurant.restaurant.service.dto.RestaurantDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author Ding
 * @since 2024.7.10.
 * description: restaurant controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestaurantApiController implements RestaurantApiDoc {
    private final RestaurantQueryService restaurantQueryService;

    private final ViewerKeyProvider viewerKeyProvider;
    private final ViewCountService viewCountService;

    @GetMapping("/v2/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetail> getRestaurantDetailWithAuth(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        String viewerKey = viewerKeyProvider.resolveViewerKey(user, req, res);
        viewCountService.countOncePerHour(ViewResourceType.POST, restaurantId, viewerKey);

        return new ResponseEntity<>(
                restaurantQueryService.getRestaurantDetail(restaurantId, user.id()),
                HttpStatus.OK
        );
    }




}
