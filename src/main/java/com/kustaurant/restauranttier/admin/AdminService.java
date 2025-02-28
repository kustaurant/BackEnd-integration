package com.kustaurant.restauranttier.admin;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final RestaurantApiService restaurantService;

    // 식당 정보 갱신
    @Transactional
    public void updateRestaurantInfo(int id, RestaurantInfoDto info) {
        Restaurant restaurant = restaurantService.findRestaurantById(id);

        setRestaurant(restaurant, info);

        restaurantService.saveRestaurant(restaurant);
    }

    private void setRestaurant(Restaurant restaurant, RestaurantInfoDto info) {
        restaurant.setRestaurantName(info.getName());
        restaurant.setRestaurantType(info.getType());
        restaurant.setRestaurantCuisine(info.getCuisine());
        restaurant.setRestaurantAddress(info.getAddress());
        restaurant.setRestaurantTel(info.getTel());
        restaurant.setRestaurantUrl(info.getUrl());
        restaurant.setRestaurantImgUrl(info.getImgUrl());
        restaurant.setRestaurantPosition(info.getPosition());
        restaurant.setRestaurantLatitude(info.getLatitude().toString());
        restaurant.setRestaurantLongitude(info.getLongitude().toString());
        if (info.getPartnershipInfo() == null || info.getPartnershipInfo().isBlank()) {
            restaurant.setPartnershipInfo(null);
        } else {
            restaurant.setPartnershipInfo(info.getPartnershipInfo());
        }
    }

    // 식당 id로 RestaurantInfo 객체 가져오기
    public Optional<RestaurantInfoDto> getRestaurantInfo(int id) {
        try {
            Restaurant restaurant = restaurantService.findRestaurantById(id);
            return Optional.of(new RestaurantInfoDto(
                    restaurant.getRestaurantName(),
                    restaurant.getRestaurantType(),
                    restaurant.getRestaurantCuisine(),
                    restaurant.getRestaurantAddress(),
                    restaurant.getRestaurantTel(),
                    restaurant.getRestaurantUrl(),
                    restaurant.getRestaurantImgUrl(),
                    restaurant.getRestaurantPosition(),
                    restaurant.getPartnershipInfo(),
                    parseDoubleOrNull(restaurant.getRestaurantLatitude()),
                    parseDoubleOrNull(restaurant.getRestaurantLongitude())));
        } catch (Exception e) {
            log.error("[AdminService][getRestaurantInfo] {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    // String을 Double 형으로 바꿀 수 있으면 바꾸고, 못 바꾸면 null 반환
    private Double parseDoubleOrNull(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 식당 정보 여러개 추가
    @Transactional
    public void addRestaurantInfos(MultiValueMap<String, String> dataMap) {
        int i = 0;
        while (dataMap.containsKey("restaurants[" + i + "].name")) {
            addRestaurantInfo((new RestaurantInfoDto(
                    dataMap.getFirst("restaurants[" + i + "].name"),
                    dataMap.getFirst("restaurants[" + i + "].type"),
                    dataMap.getFirst("restaurants[" + i + "].cuisine"),
                    dataMap.getFirst("restaurants[" + i + "].address"),
                    dataMap.getFirst("restaurants[" + i + "].tel"),
                    dataMap.getFirst("restaurants[" + i + "].url"),
                    dataMap.getFirst("restaurants[" + i + "].imgUrl"),
                    dataMap.getFirst("restaurants[" + i + "].position"),
                    dataMap.getFirst("restaurants[" + i + "].partnershipInfo"),
                    parseDoubleOrNull(dataMap.getFirst("restaurants[" + i + "].latitude")),
                    parseDoubleOrNull(dataMap.getFirst("restaurants[" + i + "].longitude"))
            )));
            i++;
        }
    }

    // 식당 정보 하나 추가
    @Transactional
    public void addRestaurantInfo(RestaurantInfoDto info) {
        Restaurant restaurant = new Restaurant();

        setRestaurant(restaurant, info);

        log.info("식당 정보 저장 완료\n{}", info);

//        restaurantService.saveRestaurant(restaurant);
    }
}
