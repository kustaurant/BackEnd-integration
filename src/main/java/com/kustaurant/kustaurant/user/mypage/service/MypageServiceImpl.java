package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.global.exception.exception.user.NoProfileChangeException;
import com.kustaurant.kustaurant.global.exception.exception.user.UserNotFoundException;
import com.kustaurant.kustaurant.admin.notice.domain.Notice;
import com.kustaurant.kustaurant.admin.notice.infrastructure.NoticeRepository;

import java.util.stream.Collectors;

import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;
import com.kustaurant.kustaurant.user.mypage.controller.response.web.MypageDataView;
import com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo.*;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.domain.PhoneNumber;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final UserRepository userRepository;
    private final UserProfileValidator validator;
    private final NoticeRepository noticeRepo;
    private final MyUserQueryRepository mypageQueryRepository;
    private final MyRestaurantQueryRepository myRestaurantQueryRepository;
    private final MyEvaluationQueryRepository myevaluationQueryRepository;
    private final MyPostQueryRepository myPostQueryRepository;

    // 1. 마이페이지 화면에서 표시될 정보들 전체 반환.
    public ProfileResponse getProfile(Long userId){
        return mypageQueryRepository.getProfile(userId);
    }

    // 2. 마이페이지 프로필 정보(변경)화면에서 로직을 검증하고 업데이트 하거나 결과를 반환
    public ProfileUpdateResponse updateUserProfile(Long userId, ProfileUpdateRequest req) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        boolean changed = false;
        if(req.nickname()==null&&req.phoneNumber()==null){
            throw new NoProfileChangeException();
        }

        if (req.nickname() != null) {
            Nickname newNick = new Nickname(req.nickname());
            validator.validateNicknameChange(user, newNick);  // 30일 룰 + 중복 체크
            user.changeNickname(newNick);
            changed = true;
        }

        if (req.phoneNumber() != null) {
            PhoneNumber newPhone = new PhoneNumber(req.phoneNumber());
            validator.validatePhoneNumberChange(newPhone); // 중복 체크
            user.changePhoneNumber(newPhone);
            changed = true;
        }

        if (!changed) {
            throw new NoProfileChangeException();
        }

        return new ProfileUpdateResponse(
                user.getNickname().getValue(),
                user.getEmail() != null ? user.getEmail() : null,
                user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null
        );
    }


    // 3. 유저가 즐겨찾기한 레스토랑 리스트들 반환 (추가한 순서)
    public List<MyRestaurantResponse> getUserFavoriteRestaurantList(Long userId) {

        return myRestaurantQueryRepository.findMyFavoritesRestaurants(userId);
    }


    // 4. 유저가 평가한 레스토랑 리스트들 반환 (평가한 날짜 순서)
    public List<MyRatedRestaurantResponse> getUserEvaluateRestaurantList(Long userId) {

        return myevaluationQueryRepository.findByUserId(userId);
    }


    // 5. 마이페이지 - 내가 작성한 게시글 목록 조회
    public List<MyPostsResponse> getUserPosts(Long userId) {

        return myPostQueryRepository.findMyPostsByUserId(userId);
    }

    // 6. 내가 스크랩한 게시글 목록 조회
    public List<MyPostsResponse> getScrappedUserPosts(Long userId) {

        return myPostQueryRepository.findMyScrappedPostsByUserId(userId);
    }

    // 7. 마이페이지 - 내가 작성한 게시글 댓글 목록 조회
    public List<MyPostCommentResponse> getCommentedUserPosts(Long userId) {

        return myPostQueryRepository.findMyPostCommentsByUserId(userId);
    }


    // 8. 모든 공지사항 목록 반환
    public List<Notice> getAllNotices() {
        return noticeRepo.findAll().stream()
                .map(notice -> new Notice(
                        notice.getTitle(),
                        notice.getHref(),
                        notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());
    }


    // 웹 마이페이지 전용
    public MypageDataView getMypageWebData(Long userId) {
        List<MyRestaurantResponse> myFavoriterestaurants = this.getUserFavoriteRestaurantList(userId);
        List<MyRatedRestaurantResponse> myRatedRestaurants = this.getUserEvaluateRestaurantList(userId);
        List<MyPostsResponse> myPosts = this.getUserPosts(userId);
        List<MyPostCommentResponse> myPostComments = this.getCommentedUserPosts(userId);
        List<MyPostsResponse> myPostScraps = this.getScrappedUserPosts(userId);

        return MypageDataView.builder()
                .restaurantFavoriteList(myFavoriterestaurants)
                .restaurantEvaluationList(myRatedRestaurants)
                .postList(myPosts)
                .postCommentList(myPostComments)
                .postScrapList(myPostScraps)
                .build();
    }
}
