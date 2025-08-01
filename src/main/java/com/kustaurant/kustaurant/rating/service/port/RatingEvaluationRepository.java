package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import java.util.List;
import java.util.Map;

public interface RatingEvaluationRepository {

    Map<Integer, List<EvaluationWithContext>> getEvaluationsByRestaurantIds(List<Integer> restaurantIds);
}
