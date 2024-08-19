package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantSituationRelation;
import com.kustaurant.restauranttier.tab3_tier.entity.Situation;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantSituationRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantSituationRelationService {
    private final RestaurantSituationRelationRepository restaurantSituationRelationRepository;

    public RestaurantSituationRelation getByRestaurantAndSituation(Restaurant restaurant, Situation situation) {
        return restaurantSituationRelationRepository.findByRestaurantAndSituation(restaurant, situation).orElse(null);
    }

    // 기존에 데이터가 있으면 업데이트하고, 없으면 새로 생성합니다.
    public void updateOrCreate(Restaurant restaurant, Situation situation, Integer AddDateCount) {
        RestaurantSituationRelation restaurantSituationRelation = getByRestaurantAndSituation(restaurant, situation);
        if (restaurantSituationRelation == null) { // 새로 생성
            restaurantSituationRelationRepository.save(new RestaurantSituationRelation(
                    AddDateCount, situation, restaurant
            ));
        } else { // 기존에 있어서 업데이트
            restaurantSituationRelation.setDataCount(restaurantSituationRelation.getDataCount() + AddDateCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelation);
        }
    }
}
