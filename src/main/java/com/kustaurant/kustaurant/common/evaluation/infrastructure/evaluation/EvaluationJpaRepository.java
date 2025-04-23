package com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, Integer>{
    boolean existsByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    List<EvaluationEntity> findByRestaurant_RestaurantIdAndStatus(Integer restaurantId, String status);

    Integer countAllByStatus(String status);

    @Query("""
    SELECT e FROM EvaluationEntity e
    WHERE e.user.id = :userId
    ORDER BY 
        CASE 
            WHEN e.updatedAt IS NOT NULL AND e.updatedAt > e.createdAt THEN e.updatedAt
            ELSE e.createdAt
        END DESC
    """)
    List<EvaluationEntity> findSortedEvaluationsByUserIdDesc(@Param("userId") Integer userId);

    List<EvaluationEntity> findByStatus(String status);
}
