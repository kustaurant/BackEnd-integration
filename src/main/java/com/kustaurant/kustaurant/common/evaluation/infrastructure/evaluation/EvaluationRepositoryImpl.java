package com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.common.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EvaluationRepositoryImpl implements EvaluationRepository {

    private final EvaluationJpaRepository jpaRepository;
    private final EvaluationJpaRepository evaluationJpaRepository;

    @Override
    public boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId);
    }

    @Override
    public List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status) {
        return evaluationJpaRepository.findByRestaurant_RestaurantIdAndStatus(restaurantId, status);
    }

    // TODO: need to delete everything below this.

    @Override
    public EvaluationEntity save(EvaluationEntity evaluation) {
        return null;
    }

    @Override
    public Optional<Evaluation> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(UserEntity UserEntity, RestaurantEntity restaurant, String status) {
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
    public Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status) {
        return Optional.empty();
    }

    @Override
    public List<Evaluation> findByStatus(String status) {
        return List.of();
    }
}
