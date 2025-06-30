package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.RestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.SituationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.RestaurantSituationRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantSituationRelationService {

    private final RestaurantSituationRelationRepository restaurantSituationRelationRepository;

    public RestaurantSituationRelationEntity getByRestaurantAndSituation(Integer restaurantId, Long situationId) {
        return restaurantSituationRelationRepository.findByRestaurantIdAndSituationId(restaurantId, situationId).orElse(null);
    }

    // 기존에 데이터가 있으면 업데이트하고, 없으면 새로 생성합니다.
    public void updateOrCreate(Integer restaurantId, Long situationId, Integer addDateCount) {
        RestaurantSituationRelationEntity restaurantSituationRelationEntity = getByRestaurantAndSituation(restaurantId, situationId);
        if (restaurantSituationRelationEntity == null) { // 새로 생성
            if (addDateCount > 0) { // 음수가 들어올수도 있어서
                restaurantSituationRelationRepository.save(new RestaurantSituationRelationEntity(
                        addDateCount, situationId, restaurantId
                ));
            }
        } else { // 기존에 있어서 업데이트
            restaurantSituationRelationEntity.setDataCount(restaurantSituationRelationEntity.getDataCount() + addDateCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelationEntity);
        }
    }
}
