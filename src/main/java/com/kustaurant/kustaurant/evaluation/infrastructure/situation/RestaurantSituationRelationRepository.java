package com.kustaurant.kustaurant.evaluation.infrastructure.situation;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationRepository extends JpaRepository<RestaurantSituationRelationEntity, Integer>, JpaSpecificationExecutor<RestaurantSituationRelationEntity> {
    Optional<RestaurantSituationRelationEntity> findByRestaurantAndSituation(RestaurantEntity restaurant, SituationEntity situationEntity);
}
