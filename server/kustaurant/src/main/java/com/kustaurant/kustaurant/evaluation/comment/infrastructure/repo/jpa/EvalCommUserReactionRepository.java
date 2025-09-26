package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvaluationCommentReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EvalCommUserReactionRepository extends JpaRepository<EvaluationCommentReactionEntity, Long> {

    @Query("select l from EvaluationCommentReactionEntity l "
            + "where l.userId = :userId and l.evalCommentId = :evalCommentId")
    Optional<EvaluationCommentReactionEntity> findByUserIdAndEvalCommentId(
            @Param("userId") Long userId,
            @Param("evalCommentId") Long evalCommentId
    );

    void deleteAllByEvalCommentId(Long evalCommentId);

    @Query("""
        select new map(r.evalCommentId as commentId, r.reaction as type)
        from EvaluationCommentReactionEntity r
        where r.userId   = :userId
          and r.evalCommentId in :commentIds
    """)
    List<Map<String, Object>> findAllByUserIdAndEvalCommentIdIn(
            @Param("userId") Long userId,
            @Param("commentIds") List<Long> commentIds);

    default Map<Long, ReactionType> toMap(Long userId, List<Long> commentIds) {
        return findAllByUserIdAndEvalCommentIdIn(userId, commentIds).stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("commentId"),
                        m -> (ReactionType) m.get("type")
                ));
    }
}
