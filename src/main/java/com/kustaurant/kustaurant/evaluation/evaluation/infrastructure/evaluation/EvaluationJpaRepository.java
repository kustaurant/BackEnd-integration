package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, Integer>{
    boolean existsByUserIdAndRestaurant_RestaurantId(Long userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurant_RestaurantIdAndStatus(Integer restaurantId, String status);

    Integer countAllByStatus(String status);

    @Query("""
    SELECT e FROM EvaluationEntity e
    WHERE e.userId = :userId
    ORDER BY 
        CASE 
            WHEN e.updatedAt IS NOT NULL AND e.updatedAt > e.createdAt THEN e.updatedAt
            ELSE e.createdAt
        END DESC
    """)
    List<EvaluationEntity> findSortedEvaluationsByUserIdDesc(@Param("userId") Long userId);

    List<EvaluationEntity> findByStatus(String status);

    List<EvaluationEntity> findByUserId(Long userId);
}
