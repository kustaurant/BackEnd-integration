package com.kustaurant.kustaurant.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
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
        return jpaRepository.existsByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId);
    }

    @Override
    public List<EvaluationEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status) {
        return evaluationJpaRepository.findByRestaurant_RestaurantIdAndStatus(restaurantId, status);
    }

    @Override
    public Integer countAllByStatus(String status) {
        return jpaRepository.countAllByStatus(status);
    }


    public List<EvaluationDomain> findByUserId(Long userId) {
        return evaluationJpaRepository.findByUser_UserId(userId).stream().map(EvaluationDomain::from).toList();
    }


    @Override
    public List<EvaluationDomain> findSortedEvaluationByUserIdDesc(Long userId) {
        return evaluationJpaRepository.findSortedEvaluationsByUserIdDesc(userId).stream()
                .map(EvaluationDomain::from)
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
