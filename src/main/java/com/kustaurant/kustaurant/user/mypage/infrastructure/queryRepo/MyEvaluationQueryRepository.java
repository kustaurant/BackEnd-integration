package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation.EvaluationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyEvaluationQueryRepository extends Repository<EvaluationEntity, Long> {
    // TODO: 연관 관계 삭제로 인해 쿼리 임시 수정
//    @Query("""
//        select distinct e
//        from EvaluationEntity e
//        join fetch e.restaurant r
//        left join fetch e.evaluationSituationEntityList es
//        left join fetch es.situation s
//        where e.userId = :userId
//          and e.status  = 'ACTIVE'
//        order by e.createdAt desc
//    """)
    @Query("""
        select distinct e
        from EvaluationEntity e
        where e.userId = :userId
          and e.status  = 'ACTIVE'
        order by e.createdAt desc
    """)
    List<EvaluationEntity> findActiveByUserId(@Param("userId") Long userId);
}
