package com.kustaurant.kustaurant.restaurant.search.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.search.service.response.RestaurantSearchResponse;
import com.kustaurant.kustaurant.restaurant.search.service.RestaurantSearchV2Service;
import com.kustaurant.kustaurant.restaurant.search.service.RestaurantSearchV3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController implements SearchApiDoc{

    private final RestaurantSearchV2Service restaurantSearchV2Service;
    private final RestaurantSearchV3Service restaurantSearchV3Service;

    @Override
    @GetMapping("/api/v2/search")
    public ResponseEntity<List<RestaurantCoreInfoDto>> searchV2(String kw, AuthUserInfo user) {
        if (kw == null || kw.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        String[] kwList = kw.split(" ");

        return ResponseEntity.ok(restaurantSearchV2Service.search(kwList, user.id()));
    }

    @Override
    @GetMapping("/api/v3/search")
    public ResponseEntity<RestaurantSearchResponse> searchV3(
            String kw, int page, int size, AuthUserInfo user
    ) {
        String[] kwList = preprocessKeyword(kw);
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(restaurantSearchV3Service.search(kwList, user.id(), pageable));
    }

    @Override
    @GetMapping("/api/v4/search")
    public ResponseEntity<RestaurantSearchResponse> searchV4(
            String kw, int page, int size, AuthUserInfo user
    ) {
        String[] kwList = preprocessKeyword(kw);
        Pageable pageable = PageRequest.of(page - 1, size);

        return ResponseEntity.ok(restaurantSearchV3Service.search(kwList, user.id(), pageable));
    }

    private static String[] preprocessKeyword(String kw) {
        if (kw == null || kw.isEmpty()) {return new String[0];}
        return kw.trim().replaceAll("\\s+", " ").split(" ");
    }
}
