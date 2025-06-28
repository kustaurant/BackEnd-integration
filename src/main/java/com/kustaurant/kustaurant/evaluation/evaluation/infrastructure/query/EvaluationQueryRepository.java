package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.query;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluationQueryRepository extends Repository<EvaluationEntity, Long> {
    @Query("""
        select e
        from EvaluationEntity e
        join fetch e.restaurant r
        where e.userId       = :userId
          and r.restaurantId = :restaurantId
          and e.status       = 'ACTIVE'
    """)
    Optional<EvaluationEntity> findActiveByUserAndRestaurant(
            @Param("userId") Long userId,
            @Param("restaurantId") Integer restaurantId);
}
