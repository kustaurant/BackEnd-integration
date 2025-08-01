package com.kustaurant.kustaurant.rating.infrastructure.jpa;

import com.kustaurant.kustaurant.rating.domain.model.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.service.port.RatingEvaluationRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class RatingEvaluationRepositoryImpl implements RatingEvaluationRepository {

    @Override
    public Map<Integer, List<EvaluationWithContext>> getEvaluationsByRestaurantIds(
            List<Integer> restaurantIds) {
        return Map.of();
    }
}
