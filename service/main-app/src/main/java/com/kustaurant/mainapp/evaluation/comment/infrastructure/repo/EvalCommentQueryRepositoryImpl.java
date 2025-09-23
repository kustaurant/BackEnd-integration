package com.kustaurant.mainapp.evaluation.comment.infrastructure.repo;

import com.kustaurant.mainapp.common.util.TimeAgoResolver;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.mainapp.evaluation.comment.infrastructure.repo.projection.CommentProjection;
import com.kustaurant.mainapp.evaluation.comment.service.port.EvalCommentQueryRepository;
import com.kustaurant.mainapp.common.util.UserIconResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvalCommentQueryRepositoryImpl implements EvalCommentQueryRepository {
    private final EntityManager em;

    @Override
    public EvalCommentResponse fetchEvalCommentWithWriter(Long evalCommentId, Long currentUserId) {
        TypedQuery<CommentProjection> query = em.createQuery("""
    select new com.kustaurant.mainapp.evaluation.comment.infrastructure.repo.projection.CommentProjection(
               c.id,
               u.nickname,
               u.stats.ratedRestCnt,
               c.createdAt,
               c.body,
               (select r.reaction
                  from EvalCommUserReactionEntity r
                 where r.evalCommentId = c.id
                   and r.userId        = :currentUserId),
               c.likeCount,
               c.dislikeCount,
               case when c.userId = :currentUserId then true else false end
           )
      from EvalCommentEntity c
     inner join UserEntity   u on u.id = c.userId
     where c.id = :evalCommentId
    """, CommentProjection.class);

        query.setParameter("evalCommentId",  evalCommentId);
        query.setParameter("currentUserId",  currentUserId);

        CommentProjection p = query.getSingleResult();

        // 아이콘 결정
        String iconUrl = UserIconResolver.resolve(p.writerEvalCnt());

        return new EvalCommentResponse(
                p.commentId(),
                iconUrl,
                p.writerNickname(),
                TimeAgoResolver.toKor(p.createdAt()),
                p.commentBody(),
                p.myReaction(),
                p.likeCnt().intValue(),
                p.dislikeCnt().intValue(),
                p.isMine()
        );
    }
}
