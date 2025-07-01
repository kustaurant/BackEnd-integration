package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.evaluation.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantRatingService {

    private final RestaurantRepository restaurantRepository;

    private final EvaluationConstants evaluationConstants;

    @Transactional
    public void afterEvaluationCreated(Integer restaurantId, Double score) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);

        int tier = calculateTier(restaurant.getRestaurantEvaluationCount(), restaurant.getAvgScore());

        restaurant.afterEvaluationCreated(score, tier);

        restaurantRepository.updateStatistics(restaurant);
    }

    @Transactional
    public void afterReEvaluated(Integer restaurantId, Double preScore, Double postScore) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);

        int tier = calculateTier(restaurant.getRestaurantEvaluationCount(), restaurant.getAvgScore());

        restaurant.afterReEvaluated(preScore, postScore, tier);

        restaurantRepository.updateStatistics(restaurant);
    }

    private int calculateTier(Integer evalCount, Double avgScore) {
        return evaluationConstants.calculateRestaurantTier(evalCount, avgScore);
    }
}
