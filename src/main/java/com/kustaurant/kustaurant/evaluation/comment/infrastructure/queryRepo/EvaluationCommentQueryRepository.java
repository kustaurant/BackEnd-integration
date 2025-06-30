package com.kustaurant.kustaurant.evaluation.comment.infrastructure.queryRepo;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.projection.EvalCommentProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationCommentQueryRepository extends Repository<EvaluationEntity, Integer> {

    @Query("""
        SELECT  e.id                              AS commentId,
                e.evaluationScore                 AS score,
                e.commentBody                     AS body,
                e.commentImgUrl                   AS imgUrl,
                e.createdAt                       AS writtenAt,

                u.id                              AS userId,
                u.nickname.value                  AS nickname,
                s.ratedRestCnt                    AS ratedCnt,

                SIZE(e.restaurantCommentLikeList)     AS likeCnt,
                SIZE(e.restaurantCommentDislikeList)  AS dislikeCnt
        FROM   EvaluationEntity e
        JOIN   UserEntity         u  ON u.id = e.userId
        JOIN   u.stats            s
        WHERE  e.restaurantId = :rid
          AND  e.status = 'ACTIVE'
    """)
    List<EvalCommentProjection> fetchEvalComments(@Param("rid") Integer restaurantId);
}
