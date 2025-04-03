package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.constants.MapConstants;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierMapDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.enums.LocationEnum;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantJpaRepository;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantTierService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final EvaluationService evaluationService;

    // Cuisine, Situation, Location 조건에 맞는 식당 리스트를 반환
    public List<RestaurantTierDTO> findByConditions(
            List<String> cuisines, List<Integer> situations, List<String> locations,
            Integer tierInfo, boolean isOrderByScore, @Nullable Integer userId
    ) {
        List<Restaurant> restaurants = restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisines, locations, situations, "ACTIVE", tierInfo, isOrderByScore));

        if (restaurants.isEmpty()) {
            return new ArrayList<>();
        }
        List<RestaurantTierDTO> responseList = new ArrayList<>();


        // 순위
        int ranking = 1;
        for (Restaurant restaurant : restaurants) {
            responseList.add(
                    RestaurantTierDTO.convertRestaurantToTierDTO(
                            restaurant,
                            ranking++,
                            evaluationService.isUserEvaluated(userId, restaurant.getRestaurantId()),
                            restaurantFavoriteService.isUserFavorite(userId, restaurant.getRestaurantId())
                    )
            );
        }

        return responseList;
    }

    // 위 함수의 페이징 버전
    public List<RestaurantTierDTO> findByConditionsWithPage(
            List<String> cuisines, List<Integer> situations, List<String> locations,
            Integer tierInfo, boolean isOrderByScore, int page, int size, @Nullable Integer userId
    ) {
        Pageable pageable = PageRequest.of(page, size);

        List<Restaurant> restaurants = restaurantRepository.findAll(RestaurantSpecification.withCuisinesAndLocationsAndSituations(cuisines, locations, situations, "ACTIVE", tierInfo, isOrderByScore), pageable);

        if (restaurants.isEmpty()) {
            return new ArrayList<>();
        }
        List<RestaurantTierDTO> responseList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            try {
                // 순위
                Integer ranking = null;
                Restaurant restaurant = restaurants.get(i);
                if (restaurant.getMainTier() > 0) {
                    ranking = page * size + i + 1;
                }
                responseList.add(
                        RestaurantTierDTO.convertRestaurantToTierDTO(
                                restaurant,
                                ranking,
                                evaluationService.isUserEvaluated(userId, restaurant.getRestaurantId()),
                                restaurantFavoriteService.isUserFavorite(userId, restaurant.getRestaurantId())
                        )
                );
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return responseList;
    }

    public RestaurantTierMapDTO getRestaurantTierMapDto(
            List<String> cuisines, List<Integer> situations, List<String> locations, @Nullable Integer userId
    ) {
        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<RestaurantTierDTO> tieredRestaurantTierDTOs = findByConditions(cuisines, situations, locations, 1, false, userId);
        List<RestaurantTierDTO> nonTieredRestaurantTierDTOs = findByConditions(cuisines, situations, locations, -1, false, userId);

        // 2. 응답 생성하기
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();
        // 2.1 즐겨찾기 리스트
        response.setFavoriteRestaurants(restaurantFavoriteService.getFavoriteRestaurantDtoList(userId));
        // 2.2 티어가 있는 식당 리스트
        response.setTieredRestaurants(tieredRestaurantTierDTOs);
        // 2.3 티어가 없는 식당 리스트
        List<RestaurantTierDTO> nonTier16 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 0)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier17 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 1)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier18 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 2)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier19 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 3)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        response.insertZoomAndRestaurants(16, nonTier16);
        response.insertZoomAndRestaurants(17, nonTier17);
        response.insertZoomAndRestaurants(18, nonTier18);
        response.insertZoomAndRestaurants(19, nonTier19);
        // 2.4 폴리곤 좌표 리스트의 리스트
        if (locations != null && !locations.contains(("ALL"))) {
            try {
                for (int i = 0; i < MapConstants.LIST_OF_COORD_LIST.size(); i++) {
                    LocationEnum locationEnum = LocationEnum.valueOf("L" + (i + 1));
                    if (locations.contains(locationEnum.getValue())) {
                        response.getSolidPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    } else {
                        response.getDashedPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
            }
        } else {
            response.setSolidPolygonCoordsList(MapConstants.LIST_OF_COORD_LIST);
        }
        // 2.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(MapConstants.findMinMaxCoordinates(response.getSolidPolygonCoordsList()));

        return response;
    }
}
