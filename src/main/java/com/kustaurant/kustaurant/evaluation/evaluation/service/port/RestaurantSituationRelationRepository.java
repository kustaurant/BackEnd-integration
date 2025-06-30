package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.RestaurantSituationRelation;
import java.util.Optional;

public interface RestaurantSituationRelationRepository {

    Optional<RestaurantSituationRelation> findByRestaurantIdAndSituationId(Integer restaurantId, Long situationId);

    Long create(RestaurantSituationRelation restaurantSituationRelation);

    void updateDataCount(RestaurantSituationRelation relation);
}
