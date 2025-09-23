package com.kustaurant.mainapp.restaurant.query.search;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchApiController implements SearchApiDoc {

    private final RestaurantSearchService restaurantSearchService;

    @GetMapping("/api/v2/search")
    public ResponseEntity<List<RestaurantCoreInfoDto>> search(
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @AuthUser AuthUserInfo user
    ) {
        if (kw == null || kw.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        String[] kwList = kw.split(" ");

        return ResponseEntity.ok(restaurantSearchService.search(kwList, user.id()));
    }
}
