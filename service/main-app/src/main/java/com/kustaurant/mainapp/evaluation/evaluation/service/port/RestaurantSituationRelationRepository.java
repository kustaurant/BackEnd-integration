package com.kustaurant.mainapp.evaluation.evaluation.service.port;

import com.kustaurant.mainapp.evaluation.evaluation.domain.RestaurantSituationRelation;
import java.util.Optional;

public interface RestaurantSituationRelationRepository {

    Optional<RestaurantSituationRelation> findByRestaurantIdAndSituationId(Long restaurantId, Long situationId);

    Long create(RestaurantSituationRelation restaurantSituationRelation);

    void changeDataCount(RestaurantSituationRelation relation);
}
