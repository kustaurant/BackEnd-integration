package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.kustaurant.global.exception.exception.business.NoProfileChangeException;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.service.web.PostCommandService;
import com.kustaurant.kustaurant.post.post.service.web.PostScrapService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.restaurant.service.constants.RestaurantConstants;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageApiService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@Tag("middleTest")
class MypageApiServiceIT {

    @Autowired MypageApiService mypageApiService;
    @Autowired EvaluationCommandService evaluationService;
    @Autowired RestaurantFavoriteService restaurantFavoriteService;
    @Autowired PostCommandService postService;
    @Autowired PostScrapService postScrapService;
    @Autowired PostCommentService postCommentService;

    @Autowired UserRepository userRepo;

    private User user1;
    private User user2;

    @BeforeAll
    void init(){
        user1 = userRepo.save(
                User.builder()
                        .loginApi("NAVER")
                        .providerId("user1providerId")
                        .nickname(new Nickname("wcwdfu"))
                        .email("test@test.com")
                        .status(UserStatus.ACTIVE)
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now().minusMonths(2))
                        .stats(UserStats.builder()
                                .ratedRestCnt(1)
                                .commPostCnt(1)
                                .build())
                        .build());
        user2 = userRepo.save(
                User.builder()
                        .loginApi("APPLE")
                        .providerId("user2providerId")
                        .nickname(new Nickname("kustoman"))
                        .email("test2@test.com")
                        .status(UserStatus.ACTIVE)
                        .role(UserRole.USER)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    // 1
    @Tag("middleTest") @Test @DisplayName("프로필 조회 정상동작")
    void get_profile() {
        // g
        // w
        var response = mypageApiService.getProfile(user1.getId());
        // t
        assertThat(response.nickname()).isEqualTo("wcwdfu");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    // 2
    @Test @DisplayName("마이페이지 정보 조회 정상동작")
    void get_mypage_info() {
        // g
        // w
        var response = mypageApiService.getMypageInfo(user1.getId());
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
                mypageApiService.updateUserProfile(user1.getId(), noChangeReq)
        ).isInstanceOf(NoProfileChangeException.class);
    }

    // 4
    @Test @DisplayName("프로필 수정 시 닉네임과 전화번호가 정상 변경된다")
    void updateProfile_success() {
        //g
        var req = new ProfileUpdateRequest("wcwdfu2", "01099998888");
        //w
        var res = mypageApiService.updateUserProfile(user1.getId(), req);
        //t
        assertThat(res.nickname()).isEqualTo("wcwdfu2");
        assertThat(res.phoneNumber()).isEqualTo("01099998888");
    }

    // 5
    @Test @DisplayName("닉네임만 변경하면 정상 업데이트된다")
    void updateProfile_nicknameOnly_success() {
        //g
        var req = new ProfileUpdateRequest("wcwdfu2", null);
        //w
        var res = mypageApiService.updateUserProfile(user1.getId(), req);
        //t
        assertThat(res.nickname()).isEqualTo("wcwdfu2");
    }

    // 5
    @Test @DisplayName("전화번호만 변경하면 정상 업데이트된다")
    void updateProfile_phoneNumOnly_success() {
        //g
        var req = new ProfileUpdateRequest(null, "01099998888");
        //w
        var res = mypageApiService.updateUserProfile(user1.getId(), req);
        //t
        assertThat(res.phoneNumber()).isEqualTo("01099998888");
    }

    // 6
    @Test @DisplayName("즐겨찾기한 식당 조회 정상동작")
    void should_return_favorite_restaurants_successfully() {
        //g
        restaurantFavoriteService.addFavorite(user1.getId(),1);
        restaurantFavoriteService.addFavorite(user1.getId(),2);
        restaurantFavoriteService.addFavorite(user1.getId(),3);
        restaurantFavoriteService.addFavorite(user1.getId(),4);
        restaurantFavoriteService.addFavorite(user1.getId(),5);
        //w
        var res = mypageApiService.getUserFavoriteRestaurantList(user1.getId());
        //t
        assertEquals(5, res.size());
        assertThat(res.get(0).restaurantName()).isEqualTo("한식당1");
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
        EvaluationDTO dto2 = new EvaluationDTO(
                        1.5,
                        List.of(),
                        null,
                        "맛없네요!",
                        List.of(new RestaurantConstants.StarComment(1, "신선하지 않네요")),
                        null
                );

        evaluationService.evaluate(user1.getId(), 1, dto1);
        evaluationService.evaluate(user1.getId(), 2, dto2);
        //w
        var res = mypageApiService.getUserEvaluateRestaurantList(user1.getId());
        //t
        assertEquals(2, res.size());
        assertThat(res.get(0).restaurantName()).isEqualTo("한식당1");
    }

    // 8
    @Test
    @DisplayName("내가 작성한 게시글 목록 조회(최신순) 정상 동작")
    void should_return_user_posts_successfully() {
        // g
        Post post1 = postService.create("테스트게시글1", "자유게시판", "테스트내용1", user1.getId());
        Post post2 = postService.create("테스트게시글2", "자유게시판", "테스트내용2", user1.getId());
        Post post3 = postService.create("테스트게시글3", "건의게시판", "테스트내용3", user1.getId());

        // w
        var res = mypageApiService.getUserPosts(user1.getId());

        // t
        assertEquals(3, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).postCategory()).isEqualTo("자유게시판");
        assertThat(res.get(2).postImgUrl()).isNull();
    }

    // 9
    @Test
    @DisplayName("내가 스크랩한 게시글 목록 조회 정상 동작")
    void should_return_scrapped_posts_successfully() {
        // g
        Post post1 = postService.create("테스트게시글1", "자유게시판", "테스트내용1", user2.getId());
        Post post2 = postService.create("테스트게시글2", "자유게시판", "테스트내용2", user2.getId());
        Post post3 = postService.create("테스트게시글3", "건의게시판", "테스트내용3", user2.getId());
        postScrapService.toggleScrap(user1.getId(), post1.getId());
        postScrapService.toggleScrap(user1.getId(), post2.getId());
        postScrapService.toggleScrap(user1.getId(), post3.getId());
        // w
        var res = mypageApiService.getScrappedUserPosts(user1.getId());
        // t
        assertEquals(3, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).postCategory()).isEqualTo("자유게시판");
        assertThat(res.get(2).postImgUrl()).isNull();
    }

    // 10
    @Test
    @DisplayName("내가 작성한 게시글 댓글 목록 조회 정상 동작")
    void should_return_commented_posts_successfully() {
        // g
        Post post1 = postService.create("테스트게시글1", "자유게시판", "테스트내용1", user2.getId());
        Post post2 = postService.create("테스트게시글2", "건의게시판", "테스트내용2", user2.getId());
        postCommentService.createComment("게시글1-댓글1",post1.getId(),null,user1.getId());
        postCommentService.createComment("게시글2-댓글1",post2.getId(),null,user1.getId());
//        postCommentService.createComment("게시글2-댓글1",post1.getId(),null,user2.getId());
//        postCommentService.createComment("게시글2-댓글1-대댓글1",post1.getId(),null,user1.getId());
        // w
        var res = mypageApiService.getCommentedUserPosts(user1.getId());
        // t
        assertEquals(2, res.size());
        assertThat(res.get(0).postTitle()).isEqualTo("테스트게시글1");
        assertThat(res.get(1).postcommentBody()).isEqualTo("게시글2-댓글1");
        assertThat(res.get(1).postCategory()).isEqualTo("건의게시판");
    }




}