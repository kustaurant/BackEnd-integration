package com.kustaurant.kustaurant.common.restaurant.infrastructure.situation;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationRepository extends JpaRepository<RestaurantSituationRelation, Integer>, JpaSpecificationExecutor<RestaurantSituationRelation> {
    Optional<RestaurantSituationRelation> findByRestaurantAndSituation(RestaurantEntity restaurant, Situation situation);
}
