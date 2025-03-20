package com.kustaurant.kustaurant.common.evaluation.service.port;

import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    EvaluationDomain getByUserAndRestaurant(User user, RestaurantDomain restaurant);


    // TODO: need to delete everything below this
    Evaluation save(Evaluation evaluation);
    Optional<Evaluation> findByUserAndRestaurant(User user, RestaurantEntity restaurant);
    Optional<Evaluation> findByUserAndRestaurantAndStatus(User user, RestaurantEntity restaurant, String status);

    Integer countByRestaurant(RestaurantEntity restaurant);

    Integer countAllByStatus(String status);

    Optional<Evaluation> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<Evaluation> findByStatus(String status);
}
