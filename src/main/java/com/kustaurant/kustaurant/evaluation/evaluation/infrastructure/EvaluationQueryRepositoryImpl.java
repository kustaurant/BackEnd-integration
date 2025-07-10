package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvaluationQueryJpaRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationQueryRepositoryImpl implements EvaluationQueryRepository {

    private final EvaluationQueryJpaRepository jpaRepository;

    @Override
    public Evaluation findActiveById(Long id) {
        return jpaRepository.findByIdAndStatus(id, "ACTIVE")
                .map(EvaluationEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(EVALUATION_NOT_FOUND, id, "평가"));
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public boolean existsByRestaurantAndEvaluation(Integer restaurantId, Long evaluationId) {
        if (restaurantId == null || evaluationId == null) {
            return false;
        }
        return jpaRepository.existsByRestaurantIdAndId(restaurantId, evaluationId);
    }

    @Override
    public Optional<Evaluation> findActiveByUserAndRestaurant(Long userId, Integer restaurantId) {
        return jpaRepository.findByUserIdAndRestaurantIdAndStatus(userId, restaurantId, "ACTIVE")
                .map(EvaluationEntity::toModel);
    }

    @Override
    public int countByStatus(String status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByCreatedAtDesc(Integer restaurantId) {
        return jpaRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(EvaluationEntity::toModel)
                .toList();
    }

    @Override
    public List<Evaluation> findByRestaurantIdOrderByLikeCountDesc(Integer restaurantId) {
        return jpaRepository.findByRestaurantIdOrderByLikeCountDesc(restaurantId)
                .stream()
                .map(EvaluationEntity::toModel)
                .toList();
    }
}
