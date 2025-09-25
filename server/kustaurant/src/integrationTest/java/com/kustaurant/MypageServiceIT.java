package com.kustaurant;

import com.kustaurant.kustaurant.KustaurantApplication;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.kustaurant.global.exception.exception.user.NoProfileChangeException;
import com.kustaurant.kustaurant.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.service.PostService;
import com.kustaurant.kustaurant.post.post.service.PostScrapService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRatedRestaurantResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KustaurantApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class MypageServiceIT {

    @Autowired MypageService mypageService;
    @Autowired EvaluationCommandService evaluationService;
    @Autowired RestaurantFavoriteService restaurantFavoriteService;
    @Autowired PostService postService;
    @Autowired PostScrapService postScrapService;
    @Autowired PostCommentService postCommentService;

    @Autowired UserRepository userRepo;

    @MockBean StringRedisTemplate stringRedisTemplate;
    @MockBean FindByIndexNameSessionRepository<Session> sessionRepository;

    private User user1;
    private User user2;

    @BeforeAll
    void init(){
        user1 = userRepo.save(
                User.builder()
                        .loginApi(LoginApi.NAVER)
                        .providerId("user1providerId")
                        .nickname(new Nickname("wcwdfu"))
                        .email("test@test.com")
                        .status(UserStatus.ACTIVE)
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now().minusMonths(2))
                        .updatedAt(LocalDateTime.now().minusMonths(2))
                        .stats(UserStats.builder()
                                .ratedRestCnt(1)
                                .commPostCnt(1)
                                .build())
                        .build());
        user2 = userRepo.save(
                User.builder()
                        .loginApi(LoginApi.APPLE)
                        .providerId("user2providerId")
                        .nickname(new Nickname("kustoman"))
                        .email("test2@test.com")
                        .status(UserStatus.ACTIVE)
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
    }

    // 1
    @Test @DisplayName("프로필 조회 정상동작")
    void get_profile() {
        // g
        // w
        var response = mypageService.getProfile(user1.getId());
        // t
        assertThat(response.nickname()).isEqualTo("wcwdfu");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    // 2
    @Test @DisplayName("마이페이지 정보 조회 정상동작")
    void get_mypage_info() {
        // g
        // w
        var response = mypageService.getProfile(user1.getId());
        // t
        assertThat(response.nickname()).isEqualTo("wcwdfu");
        assertThat(response.evalCnt()).isEqualTo(1);
        assertThat(response.postCnt()).isEqualTo(1);
    }

    // 3
    @Test @DisplayName("변경사항이 없으면 NoProfileChangeException이 발생한다")
    void updateProfile_noChange() {
        // g
        var noChangeReq = new ProfileUpdateRequest(null, null);
        // w + t
        assertThatThrownBy(() ->
                mypageService.updateUserProfile(user1.getId(), noChangeReq)
        ).isInstanceOf(NoProfileChangeException.class);
    }

    // 4
//    @Test @DisplayName("프로필 수정 시 닉네임과 전화번호가 정상 변경된다")
//    void updateProfile_success() {
//        //g
//        var req = new ProfileUpdateRequest("wcwdfu2", "01099998888");
//        //w
//        var res = mypageService.updateUserProfile(user1.getId(), req);
//        //t
//        assertThat(res.nickname()).isEqualTo("wcwdfu2");
//        assertThat(res.phoneNumber()).isEqualTo("01099998888");
//    }

    // 5
//    @Test @DisplayName("닉네임만 변경하면 정상 업데이트된다")
//    void updateProfile_nicknameOnly_success() {
//        //g
//        var req = new ProfileUpdateRequest("wcwdfu2", null);
//        //w
//        var res = mypageService.updateUserProfile(user1.getId(), req);
//        //t
//        assertThat(res.nickname()).isEqualTo("wcwdfu2");
//    }

    // 5
    @Test @DisplayName("전화번호만 변경하면 정상 업데이트된다")
    void updateProfile_phoneNumOnly_success() {
        //g
        var req = new ProfileUpdateRequest(null, "01099998888");
        //w
        var res = mypageService.updateUserProfile(user1.getId(), req);
        //t
        assertThat(res.phoneNumber()).isEqualTo("01099998888");
    }

    // 6
    @Test @DisplayName("즐겨찾기한 식당 조회 정상동작")
    void should_return_favorite_restaurants_successfully() {
        //g
        restaurantFavoriteService.addFavorite(user1.getId(),1L);
        restaurantFavoriteService.addFavorite(user1.getId(),2L);
        restaurantFavoriteService.addFavorite(user1.getId(),3L);
        restaurantFavoriteService.addFavorite(user1.getId(),4L);
        restaurantFavoriteService.addFavorite(user1.getId(),5L);
        //w
        var res = mypageService.getUserFavoriteRestaurantList(user1.getId());
        //t
        assertEquals(5, res.size());
        assertThat(res)
                .extracting(MyRestaurantResponse::restaurantName)
                .contains("한식당1");
    }

    // 7
    @Test @DisplayName("평가한 식당 조회 정상동작")
    void should_return_evaluated_restaurants_successfully() {
        //g
        EvaluationDTO dto1 = new EvaluationDTO(
                        4.5,
                        List.of(1L, 3L, 7L),
                        "http://test.com/img1.png",
                        "맛있어요!",
                        List.of(new RestaurantConstants.StarComment(6, "신선해요")),
                        null
                );
        evaluationService.evaluate(user1.getId(), 1L, dto1);

        EvaluationDTO dto2 = new EvaluationDTO(
                        1.5,
                        List.of(),
                        null,
                        "맛없네요!",
                        List.of(new RestaurantConstants.StarComment(1, "신선하지 않네요")),
                        null
                );
        evaluationService.evaluate(user1.getId(), 2L, dto2);
        //w
        List<MyRatedRestaurantResponse> res = mypageService.getUserEvaluateRestaurantList(user1.getId());
        //t
        assertThat(res).hasSize(2)
                .extracting(MyRatedRestaurantResponse::restaurantName)
                .containsExactlyInAnyOrder("한식당1", "한식당2");
    }

    // 8
    @Test
    @DisplayName("내가 작성한 게시글 목록 조회(최신순) 정상 동작")
    void should_return_user_posts_successfully() {
        // g
        PostRequest req1 = new PostRequest("테스트게시글1", PostCategory.FREE, "테스트내용1");
        PostRequest req2 = new PostRequest("테스트게시글2", PostCategory.FREE, "테스트내용2");
        PostRequest req3 = new PostRequest("테스트게시글3", PostCategory.SUGGESTION, "테스트내용3");
        Post post1 = postService.create(req1, user1.getId());
        Post post2 = postService.create(req2, user1.getId());
        Post post3 = postService.create(req3, user1.getId());

        // w
        var res = mypageService.getUserPosts(user1.getId());

        // t
        assertEquals(3, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).postCategory().getKoreanCategory()).isEqualTo("자유게시판");
        assertThat(res.get(2).postImgUrl()).isNull();
    }

    // 9
    @Test
    @DisplayName("내가 스크랩한 게시글 목록 조회 정상 동작")
    void should_return_scrapped_posts_successfully() {
        // g
        PostRequest req1 = new PostRequest("테스트게시글1", PostCategory.FREE, "테스트내용1");
        Post post1 = postService.create(req1, user2.getId());
        postScrapService.toggleScrapWithCount(post1.getId(), user1.getId(),true);

        PostRequest req2 = new PostRequest("테스트게시글2", PostCategory.FREE, "테스트내용2");
        Post post2 = postService.create(req2, user2.getId());
        postScrapService.toggleScrapWithCount(post2.getId(), user1.getId(),true);

        PostRequest req3 = new PostRequest("테스트게시글3", PostCategory.SUGGESTION, "테스트내용3");
        Post post3 = postService.create(req3, user2.getId());
        postScrapService.toggleScrapWithCount(post3.getId(), user1.getId(),true);
        // w
        var res = mypageService.getScrappedUserPosts(user1.getId());
        // t
        assertEquals(3, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).postCategory()).isEqualTo(PostCategory.FREE);
        assertThat(res.get(2).postImgUrl()).isNull();
    }

    // 10
    @Test
    @DisplayName("내가 작성한 게시글 댓글 목록 조회 정상 동작")
    void should_return_commented_posts_successfully() {
        // g
        Post post1 = postService.create(new PostRequest("테스트게시글1", PostCategory.FREE, "테스트내용1"), user2.getId());
        postCommentService.create(post1.getId(), new PostCommentRequest("게시글1-댓글1",null),user1.getId());

        Post post2 = postService.create(new PostRequest("테스트게시글2", PostCategory.SUGGESTION, "테스트내용2"), user2.getId());
        postCommentService.create(post2.getId(), new PostCommentRequest("게시글2-댓글1",null),user1.getId());

        // w
        var res = mypageService.getCommentedUserPosts(user1.getId());
        // t
        assertEquals(2, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).body()).isEqualTo("게시글2-댓글1");
        assertThat(res.get(1).postCategory()).isEqualTo(PostCategory.SUGGESTION);
    }

}