package com.kustaurant.mainapp.rating.service.port;

import com.kustaurant.mainapp.rating.domain.model.EvaluationWithContext;
import java.util.List;
import java.util.Map;

public interface RatingEvaluationRepository {

    Map<Long, List<EvaluationWithContext>> getEvaluationsByRestaurantIds(List<Long> restaurantIds);

    double getGlobalAvg();
}
