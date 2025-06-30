package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationRepository extends JpaRepository<RestaurantSituationRelationEntity, Long>, JpaSpecificationExecutor<RestaurantSituationRelationEntity> {
    Optional<RestaurantSituationRelationEntity> findByRestaurantIdAndSituationId(Integer restaurantId, Long situationId);
}
