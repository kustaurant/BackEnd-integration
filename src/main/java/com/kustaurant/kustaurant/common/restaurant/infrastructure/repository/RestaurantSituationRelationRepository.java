package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantSituationRelation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Situation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationRepository extends JpaRepository<RestaurantSituationRelation, Integer>, JpaSpecificationExecutor<RestaurantSituationRelation> {
    Optional<RestaurantSituationRelation> findByRestaurantAndSituation(Restaurant restaurant, Situation situation);
}
