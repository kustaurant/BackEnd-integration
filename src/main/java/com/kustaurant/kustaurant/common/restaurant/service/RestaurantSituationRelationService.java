package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantSituationRelation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Situation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.RestaurantSituationRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantSituationRelationService {
    private final RestaurantSituationRelationRepository restaurantSituationRelationRepository;
    private final SituationService situationService;

    public RestaurantSituationRelation getByRestaurantAndSituation(Restaurant restaurant, Situation situation) {
        return restaurantSituationRelationRepository.findByRestaurantAndSituation(restaurant, situation).orElse(null);
    }

    // 기존에 데이터가 있으면 업데이트하고, 없으면 새로 생성합니다.
    public void updateOrCreate(Restaurant restaurant, Situation situation, Integer addDateCount) {
        RestaurantSituationRelation restaurantSituationRelation = getByRestaurantAndSituation(restaurant, situation);
        if (restaurantSituationRelation == null) { // 새로 생성
            if (addDateCount > 0) { // 음수가 들어올수도 있어서
                restaurantSituationRelationRepository.save(new RestaurantSituationRelation(
                        addDateCount, situation, restaurant
                ));
            }
        } else { // 기존에 있어서 업데이트
            restaurantSituationRelation.setDataCount(restaurantSituationRelation.getDataCount() + addDateCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelation);
        }
    }

    public void createOrDelete(Restaurant restaurant, Integer situationId, Integer dataCount) {
        Situation situation = situationService.getSituation(situationId);
        if (situation == null) {
            return;
        }

        RestaurantSituationRelation restaurantSituationRelation = getByRestaurantAndSituation(restaurant, situation);
        if (dataCount == 0 && restaurantSituationRelation != null) { // 0인데 기존에 있는 경우 삭제
            restaurantSituationRelationRepository.delete(restaurantSituationRelation);
        } else if (dataCount > 0 && restaurantSituationRelation != null && !restaurantSituationRelation.getDataCount().equals(dataCount)) {
            // 기존 데이터 있는데 데이터가 다르면 업데이트
            restaurantSituationRelation.setDataCount(dataCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelation);
        } else if (dataCount > 0 && restaurantSituationRelation == null) {
            // 기존 데이터가 없으면 새로 생성
            restaurantSituationRelationRepository.save(new RestaurantSituationRelation(
                    dataCount, situation, restaurant
            ));
        }
    }
}
