package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvaluationQueryJpaRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;

import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationQueryRepositoryImpl implements EvaluationQueryRepository {

    private final EvaluationQueryJpaRepository jpa;

    @Override
    public Evaluation findActiveById(Long id) {
        return jpa.findByIdAndStatus(id, "ACTIVE")
                .map(EvaluationEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(EVALUATION_NOT_FOUND, id, "평가"));
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Long restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpa.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public boolean existsByRestaurantAndEvaluation(Long restaurantId, Long evaluationId) {
        if (restaurantId == null || evaluationId == null) {
            return false;
        }
        return jpa.existsByRestaurantIdAndId(restaurantId, evaluationId);
    }

    @Override
    public Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Long restaurantId) {
        return jpa.findByUserIdAndRestaurantIdAndStatus(userId, restaurantId, "ACTIVE")
                .map(EvaluationEntity::toModel);
    }

    @Override
    public int countByStatus(String status) {
        return jpa.countByStatus(status);
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId) {
        return jpa.findByRestaurantIdAndStatusOrderByCreatedAtDesc(restaurantId, "ACTIVE")
                .stream()
                .map(EvaluationEntity::toModel)
                .toList();
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByLikeCountDesc(Long restaurantId) {
        return jpa.findByRestaurantIdAndStatusOrderByLikeCountDesc(restaurantId, "ACTIVE")
                .stream()
                .map(EvaluationEntity::toModel)
                .toList();
    }

}
