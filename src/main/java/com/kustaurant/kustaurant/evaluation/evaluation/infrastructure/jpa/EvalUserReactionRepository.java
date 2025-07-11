package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvalUserReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EvalUserReactionRepository extends JpaRepository<EvalUserReactionEntity, Long> {
    @Query("select l from EvalUserReactionEntity l "
            + "where l.evaluationId = :evaluationId and l.userId = :userId")
    Optional<EvalUserReactionEntity> findByEvaluationIdAndUserId(
            @Param("evaluationId") Long evaluationId,
            @Param("userId") Long userId
    );

    @Query("""
        select new map(r.evaluationId as evalId, r.reaction as type)
        from EvalUserReactionEntity r
        where r.userId = :userId
          and r.evaluationId in :evalIds
    """)
    List<Map<String, Object>> findAllByUserIdAndEvaluationIdIn(
            @Param("userId") Long userId,
            @Param("evalIds") List<Long> evalIds);

    default Map<Long, ReactionType> toMap(Long userId, List<Long> evalIds) {
        return findAllByUserIdAndEvaluationIdIn(userId, evalIds).stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("evalId"),
                        m -> (ReactionType) m.get("type")
                ));
    }
}
