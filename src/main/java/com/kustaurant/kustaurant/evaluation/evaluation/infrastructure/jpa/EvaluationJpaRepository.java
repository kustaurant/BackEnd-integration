package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationJpaRepository extends JpaRepository<EvaluationEntity, Long> {

    // TODO: 궁극적으로는 이 클래스를 없애는 것이 목표 -> Query와 Command로 이동

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

    List<EvaluationEntity> findByUserId(Long userId);
}
