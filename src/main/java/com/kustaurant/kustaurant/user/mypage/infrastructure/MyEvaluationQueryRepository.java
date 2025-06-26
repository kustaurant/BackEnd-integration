package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyEvaluationQueryRepository extends Repository<EvaluationEntity, Long> {
    @Query("""
        select distinct e
        from EvaluationEntity e
        join fetch e.restaurant r
        left join fetch e.evaluationSituationEntityList es
        left join fetch es.situation s
        where e.userId = :userId
          and e.status  = 'ACTIVE'
        order by e.createdAt desc
    """)
    List<EvaluationEntity> findActiveByUserId(@Param("userId") Long userId);
}
