package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommUserReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface EvalCommUserReactionRepository extends JpaRepository<EvalCommUserReactionEntity, Long> {

    @Query("select l from EvalCommUserReactionEntity l "
            + "where l.userId = :userId and l.evalCommentId = :commentId")
    Optional<EvalCommUserReactionEntity> findByUserIdAndEvalCommentId(
            @Param("userId") Long userId,
            @Param("evalId") Long commentId
    );

    void deleteAllByEvalCommentId(Long evalCommentId);

    @Query("""
        select new map(r.evalCommentId as commentId, r.reaction as type)
        from EvalCommUserReactionEntity r
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
