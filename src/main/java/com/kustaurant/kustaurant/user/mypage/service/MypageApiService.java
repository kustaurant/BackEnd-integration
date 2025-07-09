package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.global.exception.exception.business.NoProfileChangeException;
import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.admin.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryDAO;
import java.util.stream.Collectors;
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
    private final PostQueryDAO postQueryDAO;
    private final PostCommentQueryDAO postCommentQueryDAO;

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
                    // TODO: Evaluation 연관관계 삭제 -> 주석 처리
//                    List<String> situations = e.getEvaluationSituationEntityList()
//                            .stream()
//                            .map(es -> es.getSituation().getSituationName())
//                            .toList();
                    List<String> situations = List.of();

                    return new MyRatedRestaurantResponse(
//                            e.getRestaurant().getRestaurantName(),
//                            e.getRestaurant().getRestaurantId(),
//                            e.getRestaurant().getRestaurantImgUrl(),
//                            e.getRestaurant().getRestaurantCuisine(),
                            "식당 이름",
                            1,
                            "url",
                            "Cuisine",
                            e.getEvaluationScore(),
                            e.getBody(),
                            situations
                    );
                })
                .toList();
    }


    // 6. 마이페이지 - 내가 작성한 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<MyPostsResponse> getUserPosts(Long userId) {
        return postQueryDAO.findMyWrittenPosts(userId)
                .stream()
                .map(MyPostsResponse::from)
                .collect(Collectors.toList());
    }

    // 7. 내가 스크랩한 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<MyPostsResponse> getScrappedUserPosts(Long userId) {
        return postQueryDAO.findMyScrappedPosts(userId)
                .stream()
                .map(MyPostsResponse::from)
                .collect(Collectors.toList());
    }


    // 8. 마이페이지 - 내가 작성한 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<MyPostCommentResponse> getCommentedUserPosts(Long userId) {
        return postCommentQueryDAO.findMyCommentedPostsWithDetails(userId)
                .stream()
                .map(MyPostCommentResponse::from)
                .collect(Collectors.toList());
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
