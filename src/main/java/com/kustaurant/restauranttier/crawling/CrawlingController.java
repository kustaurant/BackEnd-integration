package com.kustaurant.restauranttier.crawling;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CrawlingController {

    private final RestaurantCrawlingTask restaurantCrawlingTask;
    private final MenuCrawlingTask menuCrawlingTask;

    @PostMapping("/api/v1/crawling/restaurants")
    public ResponseEntity<List<String>> crawlingRestaurants(
            @RequestBody List<UrlAndInfo> restaurantUrlAndInfoList
    ) {
        List<String> failedUrls = new ArrayList<>();

        restaurantCrawlingTask.exec(restaurantUrlAndInfoList, failedUrls);

        return ResponseEntity.ok(failedUrls);
    }

    @PostMapping("/api/v1/crawling/menus")
    public ResponseEntity<List<Integer>> crawlingMenus(
            @RequestBody List<Integer> restaurantIdList
    ) {
        List<Integer> failedRestaurantIds = new ArrayList<>();

        menuCrawlingTask.exec(restaurantIdList, failedRestaurantIds);

        return ResponseEntity.ok(failedRestaurantIds);
    }

    @Data
    public static class UrlAndInfo {
        private String url;
        private String position;
        private String cuisine;
        private String partnershipInfo;
    }

}
