package com.kustaurant.restauranttier.crawling;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.kustaurant.restauranttier.crawling.SelectorConst.*;
import static com.kustaurant.restauranttier.crawling.SelectorConst.restaurantImgUrl;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantCrawlingTask {
    private final RestaurantRepository restaurantRepository;

    public void exec(List<CrawlingController.UrlAndInfo> urlAndInfoList, List<String> failedUrls) {
        Crawling crawling = new Crawling().setWaitTime(10).maximizeWindow();

        for (CrawlingController.UrlAndInfo urlAndInfo : urlAndInfoList) {
            try {
                Restaurant restaurant = crawlingRestaurantInfo(crawling, urlAndInfo);
                log.info("[새로운 restaurant]\n이름: {}, 타입: {}, 위치: {}, 주소: {}, 전번: {}, url: {}, imgUrl: {}, 종류: {}, 경도: {}, 위도: {}, 제휴 정보: {}",
                        restaurant.getRestaurantName(), restaurant.getRestaurantType(), restaurant.getRestaurantPosition(),
                        restaurant.getRestaurantAddress(), restaurant.getRestaurantTel(), restaurant.getRestaurantUrl(),
                        restaurant.getRestaurantImgUrl(), restaurant.getRestaurantCuisine(),
                        restaurant.getRestaurantLongitude(), restaurant.getRestaurantLatitude(), restaurant.getPartnershipInfo());

                restaurantRepository.save(restaurant);
            } catch (Exception e) {
                failedUrls.add(urlAndInfo.getUrl());
            }
        }

        crawling.quitDriver();
    }


    private Restaurant crawlingRestaurantInfo(Crawling crawling, CrawlingController.UrlAndInfo urlAndInfo) {
        Restaurant newRestaurant = new Restaurant();

        if (urlAndInfo.getUrl() == null || urlAndInfo.getUrl().isEmpty()) {
            log.error("식당 url이 없습니다. 입력 정보: {}", urlAndInfo);
            throw new RuntimeException();
        }
        crawling.openUrl(urlAndInfo.getUrl());

        // iframe 전환
        crawling.changeIframe(entryIframe);
        // **정보 주입**
        // 식당 이름
        newRestaurant.setRestaurantName(crawling.getTextBySelector(restaurantName));
        // 식당 타입 (한식이나 이런것 보다 자세한 카테고리. ex 만두, 칼국수)
        newRestaurant.setRestaurantType(crawling.getTextBySelector(restaurantType));
        // 식당 위치 (ex 건입~중문)
        newRestaurant.setRestaurantPosition(urlAndInfo.getPosition());
        // 식당 주소
        try {
            newRestaurant.setRestaurantAddress(crawling.getTextBySelector(restaurantAddress));
        } catch (Exception e) {
            newRestaurant.setRestaurantAddress("no_address");
        }
        // 식당 전화번호
        try {
            newRestaurant.setRestaurantTel(crawling.getTextBySelector(restaurantTel));
        } catch (Exception e) {
            newRestaurant.setRestaurantTel("no_tel");
        }
        // 식당 네이버 지도 링크
        newRestaurant.setRestaurantUrl(crawling.getCurrentUrl());
        // 식당 메인 이미지 url
        newRestaurant.setRestaurantImgUrl(crawling.getAttribute(restaurantImgUrl, "src"));
        // 식당 음식 카테고리 (ex 한식, 일식, 중식, ...)
        newRestaurant.setRestaurantCuisine(urlAndInfo.getCuisine());
        // 식당 경도, 위도
        String[] lonAndLat = crawling.getLonAndLat();
        newRestaurant.setRestaurantLongitude(lonAndLat[0]);
        newRestaurant.setRestaurantLatitude(lonAndLat[1]);
        // 식당 status
        newRestaurant.setStatus("ACTIVE");
        // 식당 추가 날짜 시간
        newRestaurant.setCreatedAt(LocalDateTime.now());
        // 식당 제휴 정보
        newRestaurant.setPartnershipInfo(urlAndInfo.getPartnershipInfo());

        return newRestaurant;
    }
}
