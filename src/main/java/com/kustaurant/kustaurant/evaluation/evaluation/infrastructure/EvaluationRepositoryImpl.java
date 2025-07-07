package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvaluationJpaRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EvaluationRepositoryImpl implements EvaluationRepository {

    // TODO: 궁극적으로는 이 클래스를 없애는 것이 목표 -> Query와 Command로 이동

    private final EvaluationJpaRepository jpaRepository;

    @Override
    public Integer countAllByStatus(String status) {
        return jpaRepository.countAllByStatus(status);
    }


    public List<Evaluation> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream().map(EvaluationEntity::toModel).toList();
    }


    @Override
    public List<Evaluation> findSortedEvaluationByUserIdDesc(Long userId) {
        return jpaRepository.findSortedEvaluationsByUserIdDesc(userId).stream()
                .map(EvaluationEntity::toModel)
                .toList();
    }

    @Override
    public Optional<Evaluation> findById(Long id) {
        return Optional.empty();
    }


}
