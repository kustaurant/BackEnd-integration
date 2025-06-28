package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.global.exception.exception.business.NoProfileChangeException;
import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.admin.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.*;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MypageMainResponse;
import com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo.*;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MypageApiService {
    private final UserRepository userRepository;
    private final UserProfileValidator validator;
    private final NoticeRepository noticeRepo;

    private final MyUserQueryRepository userQueryRepository;
    private final MypageMainQueryRepository mypageMainQueryRepository;
    private final MyFavoriteRestaurantQueryRepository myFavoriteRestaurantQueryRepository;
    private final MyEvaluationQueryRepository myevaluationQueryRepository;
    private final MyPostQueryRepository myPostQueryRepository;
    private final MyPostScrapQueryRepository myPostScrapQueryRepository;
    private final MyPostCommentQueryRepository myPostCommentQueryRepository;

    public User findUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    //1
    // 마이페이지 화면에서 표시될 "유저닉네임, 좋아요맛집 갯수, 내 게시글 갯수" 를 반환.
    public MypageMainResponse getMypageInfo(Long userId){
        var raw = mypageMainQueryRepository.findMypageMainByUserId(userId);
        String iconUrl = UserIconResolver.resolve(raw.evalCnt());

        return new MypageMainResponse(
                iconUrl,
                raw.nickname(),
                raw.evalCnt(),
                raw.postCnt()
        );
    }


    //2
    // 마이페이지 프로필 정보(변경)화면에서 표시될 "닉네임, 메일주소, 핸드폰번호" 를 반환
    public ProfileResponse getProfile(Long userId){
        return userQueryRepository.findProfileByUserId(userId);
    }


    //3
    // 마이페이지 프로필 정보(변경)화면에서 로직을 검증하고 업데이트 하거나 결과를 반환
    @Transactional
    public ProfileResponse updateUserProfile(Long userId, ProfileUpdateRequest req) {
        User user = findUserById(userId);

        Nickname newNick = new Nickname(req.nickname());
        PhoneNumber newPhone = new PhoneNumber(req.phoneNumber());

        boolean changed = false;

        if (!user.getNickname().equals(newNick)) {
            validator.validateNicknameChange(user, newNick);  // 30일 룰 + 중복 체크
            user.changeNickname(newNick);
            changed = true;
        }

        if (!user.getPhoneNumber().equals(newPhone)) {
            validator.validatePhoneNumberChange(newPhone); // 중복 체크
            user.changePhoneNumber(newPhone);
            changed = true;
        }

        if (!changed) {
            throw new NoProfileChangeException();
        }

        return new ProfileResponse(
                user.getNickname().getValue(),
                user.getEmail(),
                user.getPhoneNumber().getValue()
        );
    }


    // 4. 유저가 즐겨찾기한 레스토랑 리스트들 반환
    @Transactional(readOnly = true)
    public List<MyRestaurantResponse> getUserFavoriteRestaurantList(Long userId) {
        return myFavoriteRestaurantQueryRepository.findActiveFavorites(userId);
    }


    // 5. 유저가 평가한 레스토랑 리스트들 반환
    public List<MyRatedRestaurantResponse> getUserEvaluateRestaurantList(Long userId) {

        List<EvaluationEntity> list = myevaluationQueryRepository.findActiveByUserId(userId);

        return list.stream()
                .map(e -> {
                    List<String> situations = e.getEvaluationSituationEntityList()
                            .stream()
                            .map(es -> es.getSituation().getSituationName())
                            .toList();

                    return new MyRatedRestaurantResponse(
                            e.getRestaurant().getRestaurantName(),
                            e.getRestaurant().getRestaurantId(),
                            e.getRestaurant().getRestaurantImgUrl(),
                            e.getRestaurant().getRestaurantCuisine(),
                            e.getEvaluationScore(),
                            e.getCommentBody(),
                            situations
                    );
                })
                .toList();
    }



    // 6. 유저가 작성한 커뮤니티 게시글 리스트들 반환
    @Transactional(readOnly = true)
    public List<MyPostsResponse> getUserPosts(Long userId) {
        List<PostEntity> posts = myPostQueryRepository.findActivePostsByUserId(userId);

        return posts.stream()
                .map(p -> {
                    String firstImg = p.getPostPhotoEntityList().isEmpty()
                            ? null
                            : p.getPostPhotoEntityList().get(0).getPhotoImgUrl();

                    String shortBody = p.getPostBody().length() > 20
                            ? p.getPostBody().substring(0, 20)
                            : p.getPostBody();

                    LocalDateTime time = p.getUpdatedAt() != null ? p.getUpdatedAt() : p.getCreatedAt();
                    String timeAgo = TimeAgoUtil.toKor(time);

                    return new MyPostsResponse(
                            p.getPostId(),
                            p.getPostCategory(),
                            p.getPostTitle(),
                            firstImg,
                            shortBody,
                            p.getNetLikes(),
                            p.getPostCommentList().size(),
                            timeAgo
                    );
                })
                .toList();
    }


    // 7. 유저가 스크랩한 커뮤니티 게시글 리스트들 반환
    @Transactional(readOnly = true)
    public List<MyPostsResponse> getScrappedUserPosts(Long userId) {

        return myPostScrapQueryRepository.findScrappedPosts(userId)
                .stream()
                .map(this::toMyPostsResponse)
                .toList();
    }

    private MyPostsResponse toMyPostsResponse(PostEntity p) {
        String firstImg = p.getPostPhotoEntityList().isEmpty()
                ? null
                : p.getPostPhotoEntityList()
                .get(0)
                .getPhotoImgUrl();

        String shortBody = p.getPostBody().length() > 20
                ? p.getPostBody().substring(0, 20)
                : p.getPostBody();

        String timeAgo = TimeAgoUtil.toKor(
                p.getUpdatedAt() != null ? p.getUpdatedAt()
                        : p.getCreatedAt()
        );

        return new MyPostsResponse(
                p.getPostId(),
                p.getPostCategory(),
                p.getPostTitle(),
                firstImg,
                shortBody,
                p.getNetLikes(),
                p.getPostCommentList().size(),
                timeAgo
        );
    }


    // 8. 유저가 댓글단 커뮤니티 게시글 리스트들 반환
    @Transactional(readOnly = true)
    public List<MyPostCommentResponse> getCommentedUserPosts(Long userId) {

        return myPostCommentQueryRepository.findActiveCommentsByUserId(userId)
                .stream()
                .map(c -> {
                    String shortBody = c.getCommentBody().length() > 20
                            ? c.getCommentBody().substring(0, 20)
                            : c.getCommentBody();

                    String timeAgo = TimeAgoUtil.toKor(
                            c.getUpdatedAt() != null ? c.getUpdatedAt() : c.getCreatedAt()
                    );

                    return new MyPostCommentResponse(
                            c.getPost().getPostId(),
                            c.getPost().getPostCategory(),
                            c.getPost().getPostTitle(),
                            shortBody,
                            c.getLikeCount(),
                            timeAgo
                    );
                })
                .toList();
    }


    // 9. 모든 공지사항 목록 반환
    public List<NoticeDTO> getAllNotices() {
        return noticeRepo.findAll().stream()
                .map(notice -> new NoticeDTO(
                        notice.getTitle(),
                        notice.getHref(),
                        notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());
    }


}
