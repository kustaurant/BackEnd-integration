package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.global.exception.exception.auth.AccessDeniedException;
import com.kustaurant.kustaurant.mock.evaluation.FakeEvalCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvalCommCommandServiceimplTest {
    private EvalCommCommandServiceImpl service;
    private FakeEvalCommentRepository commentRepo;
    @Mock
    private EvalCommUserReactionRepository reactionRepo;

    @BeforeEach
    void init(){
        commentRepo = new FakeEvalCommentRepository();
        this.service = new EvalCommCommandServiceImpl(commentRepo, reactionRepo);

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
    @DisplayName("create()는 입력 값을 반영한 EvalComment를 저장후 반환한다.")
    void create_returnsProperEvalComment() {
        //g
        Long evalId = 2L;
        Long restaurantId = 2L;
        Long userId = 2L;
        EvalCommentRequest req = new EvalCommentRequest("테스트 댓글 본문");

        //w
        EvalComment created = service.create(evalId, restaurantId, userId, req);

        //t
        assertAll(
                () -> assertEquals(userId,        created.getUserId()),
                () -> assertEquals(restaurantId,  created.getRestaurantId()),
                () -> assertEquals(evalId,        created.getEvaluationId()),
                () -> assertEquals("테스트 댓글 본문", created.getBody()),
                () -> assertEquals(0,             created.getLikeCount()),
                () -> assertEquals(0,             created.getDislikeCount())
        );
        assertTrue(commentRepo.findById(created.getId()).isPresent());
    }

    @Test
    @DisplayName("평가 댓글을 지우면 status는 DELETED로 바꾸고, 관련 리액션 데이터는 모두 지운다")
    void delete_softDeletes_andCascade() {
        //g

        //w
        service.delete(1L,1,1L);
        //t
        // 1) 댓글이 SOFT_DELETE 상태로 바뀌었는지
        EvalComment after = commentRepo.findById(1L).orElseThrow();
        assertEquals(Status.DELETED, after.getStatus());
        // 2) 리액션 레포의 cascade 메서드가 호출됐는지
        verify(reactionRepo).deleteAllByEvalCommentId(1L);

    }

    @Test
    @DisplayName("글 작성자가 아닌 사람이 삭제요청시 forbidden exception 발생")
    void delete_byNonAuthor_throwsForbidden(){
        //g
        Long otherUserId = 99L;

        //w
        //t
        assertThrows(AccessDeniedException.class,
                ()-> service.delete(1L,1,otherUserId));
        // 1) 댓글이 그대로 ACTIVE 상태인지 확인
        EvalComment after = commentRepo.findById(1L).orElseThrow();
        assertEquals(Status.ACTIVE, after.getStatus());
        // 2) 리액션 레포가 전혀 호출되지 않았는지 확인
        verify(reactionRepo, never()).deleteAllByEvalCommentId(anyLong());
    }

}