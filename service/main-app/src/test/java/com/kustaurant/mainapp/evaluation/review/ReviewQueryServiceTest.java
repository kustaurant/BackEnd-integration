package com.kustaurant.mainapp.evaluation.review;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.common.enums.Status;
import com.kustaurant.mainapp.common.util.TimeAgoResolver;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;
import com.kustaurant.mainapp.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa.EvalUserReactionRepository;
import com.kustaurant.mainapp.mock.evaluation.FakeEvalCommentRepository;
import com.kustaurant.mainapp.mock.evaluation.FakeEvaluationRepository;
import com.kustaurant.mainapp.mock.user.FakeUserRepository;
import com.kustaurant.mainapp.user.mypage.domain.UserStats;
import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.user.user.domain.UserStatus;
import com.kustaurant.mainapp.user.user.domain.Nickname;
import com.kustaurant.mainapp.common.util.UserIconResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewQueryServiceTest {
    private FakeEvaluationRepository evalRepo;
    private FakeEvalCommentRepository evalCommentRepo;
    private FakeUserRepository userRepo;

    @Mock private EvalUserReactionRepository evalReactRepo;
    @Mock private EvalCommUserReactionRepository evalCommReactRepo;

    private ReviewQueryService service;

    private static final long RESTAURANT_ID = 1L;
    private static final long USER_ID1 = 1L;
    private static final long VIEWER_ID = 2L;
    private static final long USER_ID3 = 3L;
    private static final long USER_ID4 = 4L;
    private static final long EVAL_ID1 = 1L;
    private static final long EVAL_ID2 = 2L;
    private static final long EVAL_COMMENT_ID100 = 100L;

    @BeforeEach
    void init(){
        evalRepo = new FakeEvaluationRepository();
        evalCommentRepo = new FakeEvalCommentRepository();
        userRepo = new FakeUserRepository();
        // 유저 데이터 3개
        userRepo.save(User.builder()
                        .id(USER_ID1)
                        .nickname(new Nickname("테스트사용자1"))
                        .status(UserStatus.ACTIVE)
                        .stats(UserStats.builder()
                                .ratedRestCnt(10)
                                .build())
                        .build());
        userRepo.save(User.builder()
                        .id(VIEWER_ID)
                        .nickname(new Nickname("테스트사용자2"))
                        .status(UserStatus.ACTIVE)
                        .stats(UserStats.builder()
                                .ratedRestCnt(20)
                                .build())
                        .build());
        userRepo.save(User.builder()
                        .id(USER_ID3)
                        .nickname(new Nickname("테스트사용자3"))
                        .status(UserStatus.ACTIVE)
                        .stats(UserStats.builder()
                                .ratedRestCnt(30)
                                .build())
                        .build());
        // 평가 데이터 2개
        evalRepo.create(Evaluation.builder()
                .id(EVAL_ID1)
                .userId(USER_ID1)
                .restaurantId(RESTAURANT_ID)
                .evaluationScore(4.5)
                .commentBody("맛있는데?")
                .commentImgUrl("img1.jpg") //평가 이미지 첨부
                .likeCount(3)
                .dislikeCount(0)
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build()
        );
        evalRepo.create(Evaluation.builder()
                .id(2L)
                .userId(VIEWER_ID)
                .restaurantId(RESTAURANT_ID)
                .evaluationScore(3.0)
                .commentBody("그냥 그래")
                .commentImgUrl(null) // 평가 이미지 미첨부
                .likeCount(1)
                .dislikeCount(2)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build()
        );
        // 평가 댓글 데이터 1개
        evalCommentRepo.save(EvalComment.builder()
                .id(EVAL_COMMENT_ID100)
                .evaluationId(EVAL_ID2)
                .userId(USER_ID3)
                .restaurantId(RESTAURANT_ID)
                .body("맞아요!")
                .likeCount(1)
                .dislikeCount(0)
                .createdAt(LocalDateTime.now().minusSeconds(30))
                .status(Status.ACTIVE)
                .build()
        );

        this.service = new ReviewQueryService(evalRepo,evalCommentRepo,evalReactRepo,evalCommReactRepo,userRepo);
    }



    @Test
    @DisplayName("LATEST 정렬: 평가·댓글·리액션 매핑이 올바르게 반환된다")
    void fetch_byLatest_success() {
        //t
        try (MockedStatic<UserIconResolver> iconMock = Mockito.mockStatic(UserIconResolver.class);
             MockedStatic<TimeAgoResolver>   timeMock = Mockito.mockStatic(TimeAgoResolver.class)) {

            iconMock.when(() -> UserIconResolver.resolve(anyInt()))
                    .thenReturn("icon.png");
            timeMock.when(() -> TimeAgoResolver.toKor(any()))
                    .thenReturn("2초 전");

            /* 조회하려는 사용자가 평가1은 LIKE, 평가2-댓글1 은 DISLIKE 누름 */
            when(evalReactRepo.toMap(VIEWER_ID, List.of(1L, 2L)))
                    .thenReturn(Map.of(EVAL_ID1, ReactionType.LIKE));   // 평가1 LIKE
            when(evalCommReactRepo.toMap(eq(VIEWER_ID), anyList()))
                    .thenReturn(Map.of(EVAL_COMMENT_ID100, ReactionType.DISLIKE)); // 평가2-댓글1 DISLIKE

            // w
            List<ReviewsResponse> result = service.fetchEvaluationsWithComments(RESTAURANT_ID, VIEWER_ID, SortOption.LATEST);

            // t
            assertEquals(2, result.size(), "평가 2개 반환");

            ReviewsResponse first = result.get(0); // 최신 → eval1
            assertAll(
                    () -> assertEquals(EVAL_ID1, first.evalId()),
                    () -> assertEquals(4.5, first.evalScore()),
                    () -> assertEquals("icon.png", first.writerIconImgUrl()),
                    () -> assertEquals("맛있는데?", first.evalBody()),
                    () -> assertEquals(ReactionType.LIKE, first.reactionType()),
                    () -> assertEquals(3, first.evalLikeCount()),
                    () -> assertEquals(0, first.evalDislikeCount()),
                    () -> assertEquals(0, first.evalCommentList().size())
            );

            // 두 번째 평가는 reactionType == null/commentImgUrl == null
            ReviewsResponse second = result.get(1);
            assertAll(
                    ()->assertNull(second.reactionType()),
                    ()->assertNull(second.evalImgUrl()),
                    ()->assertEquals(1, second.evalCommentList().size()),
                    ()->assertEquals(ReactionType.DISLIKE, second.evalCommentList().get(0).reactionType())
            );

            /* ── 5) Mock 상호작용 검증 ─────────────────── */
            verify(evalReactRepo).toMap(VIEWER_ID, List.of(1L, 2L));
            verify(evalCommReactRepo).toMap(eq(VIEWER_ID), argThat(ids -> ids.contains(100L)));
            verifyNoMoreInteractions(evalReactRepo, evalCommReactRepo);
        }
    }

}