package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommUserReactionEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.mock.FakeEvalCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvalCommUserReactionServiceImplTest {
    private EvalCommUserReactionServiceImpl service;
    private FakeEvalCommentRepository commentRepo;
    @Mock
    private EvalCommUserReactionRepository reactionRepo;
    @BeforeEach
    void init(){
        commentRepo = new FakeEvalCommentRepository();
        this.service = new EvalCommUserReactionServiceImpl(reactionRepo, commentRepo);

        commentRepo.save(EvalComment.builder()
                .id(1L)
                .userId(1L)
                .restaurantId(1)
                .evaluationId(1L)
                .body("저는 테스트용 평가에 달린 댓글 이에요")
                .status(Status.ACTIVE)
                .likeCount(10)
                .dislikeCount(2)
                .build());
    }

    @Test
    @DisplayName("1.처음 좋아요를 누르면 like +1, 리액션 저장")
    void toggle_firstLike_insertsLike() {
        //g
        Long userId=2L;
        Long evalCommentId=1L;

        //w
        EvalCommentReactionResponse res = service.toggleReaction(userId, evalCommentId, ReactionType.LIKE);

        //t
        assertAll(
                ()-> assertEquals(ReactionType.LIKE, res.reaction()),
                ()->assertEquals(11, res.likeCount()),
                ()->assertEquals(2, res.dislikeCount())
        );
        // 댓글 객체 상태도 같이 확인
        EvalComment stored = commentRepo.findById(evalCommentId).orElseThrow();
        assertEquals(11,stored.getLikeCount());
        assertEquals(2,stored.getDislikeCount());
        // 리액션 저장 호출 검증
        verify(reactionRepo).save(any());
    }

    @Test
    @DisplayName("2.같은 좋아요를 또 누르면 좋아요 취소 → like -1, 리액션 삭제")
    void toggle_cancelLike_deletesReaction() {
        // g 기존 LIKE 리액션 하나 존재
        Long otherUserId=2L;
        Long evalCommentId=1L;
        EvalCommUserReactionEntity existing =
                new EvalCommUserReactionEntity(otherUserId, evalCommentId, ReactionType.LIKE);
        when(reactionRepo.findByUserIdAndEvalCommentId(otherUserId, evalCommentId))
                .thenReturn(Optional.of(existing));

        EvalComment stored = commentRepo.findById(1L).orElseThrow();

        // when
        EvalCommentReactionResponse res = service.toggleReaction(otherUserId, evalCommentId, ReactionType.LIKE);

        // then
        assertAll(
                () -> assertNull(res.reaction()), // 취소해서 반응은 null 상태
                () -> assertEquals(9, res.likeCount()),
                () -> assertEquals(2, res.dislikeCount())
        );
        assertEquals(9, stored.getLikeCount());

        verify(reactionRepo).delete(existing);
        verifyNoMoreInteractions(reactionRepo);
    }


    @Test
    @DisplayName("3.LIKE → DISLIKE 전환: like-1, dislike+1, 리액션 업데이트")
    void toggle_switchLikeToDislike_updatesReaction() {
        // given: 기존 LIKE
        Long otherUserId=2L;
        Long evalCommentId=1L;

        EvalCommUserReactionEntity existing = new EvalCommUserReactionEntity(otherUserId, evalCommentId, ReactionType.LIKE);
        when(reactionRepo.findByUserIdAndEvalCommentId(otherUserId, evalCommentId)).thenReturn(Optional.of(existing));

        EvalComment stored = commentRepo.findById(evalCommentId).orElseThrow();

        // when
        EvalCommentReactionResponse res =
                service.toggleReaction(otherUserId, evalCommentId, ReactionType.DISLIKE);

        // then
        assertAll(
                () -> assertEquals(ReactionType.DISLIKE, res.reaction()),
                () -> assertEquals(9, res.likeCount()),
                () -> assertEquals(3, res.dislikeCount())
        );
        assertEquals(9, stored.getLikeCount());
        assertEquals(3, stored.getDislikeCount());

        // 기존 엔티티의 reaction 필드가 바뀌었는지 검증
        assertEquals(ReactionType.DISLIKE, existing.getReaction());

        // 업데이트는 엔티티 필드 set 으로 끝났으니 save/delete 불필요
        verify(reactionRepo, never()).save(any());
        verify(reactionRepo, never()).delete(any());
    }

}