package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
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
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status) {
        return evaluationJpaRepository.findByRestaurantIdAndStatus(restaurantId, status);
    }

    @Override
    public Integer countAllByStatus(String status) {
        return jpaRepository.countAllByStatus(status);
    }


    public List<Evaluation> findByUserId(Long userId) {
        return evaluationJpaRepository.findByUserId(userId).stream().map(Evaluation::from).toList();
    }


    @Override
    public List<Evaluation> findSortedEvaluationByUserIdDesc(Long userId) {
        return evaluationJpaRepository.findSortedEvaluationsByUserIdDesc(userId).stream()
                .map(Evaluation::from)
                .toList();
    }


    // TODO: need to delete everything below this.

    @Override
    public EvaluationEntity save(EvaluationEntity evaluation) {
        return null;
    }

    @Override
    public Optional<EvaluationEntity> findByUserAndRestaurant(Long userId, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public Optional<EvaluationEntity> findByUserAndRestaurantAndStatus(Long userId, RestaurantEntity restaurant, String status) {
        return Optional.empty();
    }

    @Override
    public Integer countByRestaurant(RestaurantEntity restaurant) {
        return 0;
    }

    @Override
    public Optional<EvaluationEntity> findByEvaluationIdAndStatus(Integer evaluationId, String status) {
        return Optional.empty();
    }

    @Override
    public List<EvaluationEntity> findByStatus(String status) {
        return List.of();
    }
}
