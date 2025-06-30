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
    private final SituationService situationService;

    public RestaurantSituationRelationEntity getByRestaurantAndSituation(RestaurantEntity restaurant, SituationEntity situationEntity) {
        return restaurantSituationRelationRepository.findByRestaurantAndSituation(restaurant, situationEntity).orElse(null);
    }

    // 기존에 데이터가 있으면 업데이트하고, 없으면 새로 생성합니다.
    public void updateOrCreate(RestaurantEntity restaurant, SituationEntity situationEntity, Integer addDateCount) {
        RestaurantSituationRelationEntity restaurantSituationRelationEntity = getByRestaurantAndSituation(restaurant, situationEntity);
        if (restaurantSituationRelationEntity == null) { // 새로 생성
            if (addDateCount > 0) { // 음수가 들어올수도 있어서
                restaurantSituationRelationRepository.save(new RestaurantSituationRelationEntity(
                        addDateCount, situationEntity, restaurant
                ));
            }
        } else { // 기존에 있어서 업데이트
            restaurantSituationRelationEntity.setDataCount(restaurantSituationRelationEntity.getDataCount() + addDateCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelationEntity);
        }
    }

    public void createOrDelete(RestaurantEntity restaurant, Long situationId, Integer dataCount) {
        SituationEntity situationEntity = situationService.getSituation(situationId);
        if (situationEntity == null) {
            return;
        }

        RestaurantSituationRelationEntity restaurantSituationRelationEntity = getByRestaurantAndSituation(restaurant, situationEntity);
        if (dataCount == 0 && restaurantSituationRelationEntity != null) { // 0인데 기존에 있는 경우 삭제
            restaurantSituationRelationRepository.delete(restaurantSituationRelationEntity);
        } else if (dataCount > 0 && restaurantSituationRelationEntity != null && !restaurantSituationRelationEntity.getDataCount().equals(dataCount)) {
            // 기존 데이터 있는데 데이터가 다르면 업데이트
            restaurantSituationRelationEntity.setDataCount(dataCount);
            restaurantSituationRelationRepository.save(restaurantSituationRelationEntity);
        } else if (dataCount > 0 && restaurantSituationRelationEntity == null) {
            // 기존 데이터가 없으면 새로 생성
            restaurantSituationRelationRepository.save(new RestaurantSituationRelationEntity(
                    dataCount, situationEntity, restaurant
            ));
        }
    }
}
