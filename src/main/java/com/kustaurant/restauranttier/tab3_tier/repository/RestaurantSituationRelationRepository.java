package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantSituationRelation;
import com.kustaurant.restauranttier.tab3_tier.entity.Situation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RestaurantSituationRelationRepository extends JpaRepository<RestaurantSituationRelation, Integer>, JpaSpecificationExecutor<RestaurantSituationRelation> {
    Optional<RestaurantSituationRelation> findByRestaurantAndSituation(Restaurant restaurant, Situation situation);
}
