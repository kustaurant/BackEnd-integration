package com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.common.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EvaluationRepositoryImpl implements EvaluationRepository {

    private final EvaluationJpaRepository jpaRepository;

    @Override
    public EvaluationDomain getByUserAndRestaurant(User user, RestaurantDomain restaurant) {
        Integer userId = user.getUserId();
        Integer restaurantId = restaurant.getRestaurantId();

        return jpaRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId)
                .map(EvaluationEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException("요청한 evaluation가 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
    }

    // TODO: need to delete everything below this.

    @Override
    public Evaluation save(Evaluation evaluation) {
        return null;
    }

    @Override
    public Optional<Evaluation> findByUserAndRestaurant(User user, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public Optional<Evaluation> findByUserAndRestaurantAndStatus(User user, RestaurantEntity restaurant, String status) {
        return Optional.empty();
    }

    @Override
    public Integer countByRestaurant(RestaurantEntity restaurant) {
        return 0;
    }

    @Override
    public Integer countAllByStatus(String status) {
        return 0;
    }

    @Override
    public Optional<Evaluation> findByEvaluationIdAndStatus(Integer evaluationId, String status) {
        return Optional.empty();
    }

    @Override
    public List<Evaluation> findByStatus(String status) {
        return List.of();
    }
}
