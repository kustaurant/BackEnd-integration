package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.RestaurantSituationRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationJpaRepository extends JpaRepository<RestaurantSituationRelationEntity, Long>, JpaSpecificationExecutor<RestaurantSituationRelationEntity> {

    Optional<RestaurantSituationRelationEntity> findByRestaurantIdAndSituationId(Long restaurantId, Long situationId);
}
