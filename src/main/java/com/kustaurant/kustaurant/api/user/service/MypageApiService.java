package com.kustaurant.kustaurant.api.user.service;

import com.kustaurant.kustaurant.common.notice.NoticeDTO;
import com.kustaurant.kustaurant.common.notice.NoticeRepository;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.post.infrastructure.Post;
import com.kustaurant.kustaurant.common.post.infrastructure.PostComment;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrap;
import com.kustaurant.kustaurant.common.post.infrastructure.PostCommentRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.PostRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapRepository;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.user.domain.*;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MypageApiService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostCommentRepository postCommentRepository;
    private final NoticeRepository noticeRepo;

    public User findUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }

    //1
    // 마이페이지 화면에서 표시될 "유저닉네임, 좋아요맛집 갯수, 내 게시글 갯수" 를 반환.
    public MypageMainDTO getMypageInfo(Integer userid, String userAgent){
        User user = findUserById(userid);

        String iconURL = RestaurantConstants.getIconImgUrl(user, userAgent);
        String userNickname = user.getUserNickname();
        int evalListSize = user.getEvaluationList().size();
        int commuPostListSize = user.getPostList().size();

        return new MypageMainDTO(iconURL, userNickname, evalListSize, commuPostListSize);
    }


    //2
    // 마이페이지 프로필 정보(변경)화면에서 표시될 "닉네임, 메일주소, 핸드폰번호" 를 반환
    public ProfileDTO getProfileInfo(Integer userid){
        User user = findUserById(userid);

        String userNickname = user.getUserNickname();
        String userEmail = user.getUserEmail();
        String userPhoneNumber = user.getPhoneNumber();

        return new ProfileDTO(userNickname,userEmail,userPhoneNumber);
    }


    //3
    // 마이페이지 프로필 정보(변경)화면에서 로직을 검증하고 업데이트 하거나 결과를 반환
    public ProfileDTO updateUserProfile(Integer userid, ProfileDTO profileDTO) {
        User user = findUserById(userid);

        String receivedNickname = profileDTO.getNickname();
        String receivedPhoneNumber = profileDTO.getPhoneNumber();
        boolean updated = false;

        // 아무런 변경값 없이 프로필 저장하기 버튼을 누름
        if ((receivedPhoneNumber == null || receivedPhoneNumber.isEmpty() ||
                (user.getPhoneNumber() != null && user.getPhoneNumber().equals(receivedPhoneNumber)))
                && user.getUserNickname().equals(receivedNickname)) {
            throw new IllegalArgumentException("변경된 값이 없습니다.");
        }

        // 전화번호 값이 공백이 아님 (변경이 이루어진 적이 있음)
        if (StringUtils.hasText(receivedPhoneNumber)) {
            validatePhoneNum(user, receivedPhoneNumber);
            updated = true;
        }

        // 닉네임 변경이 이루어짐
        if (!user.getUserNickname().equals(receivedNickname)) {
            validateNickname(user, receivedNickname);
            updated = true;
        }

        if (!updated) {
            throw new IllegalArgumentException("프로필 변경에 실패하였습니다!");
        }

        userRepository.save(user);
        return new ProfileDTO(user.getUserNickname(), user.getUserEmail(), user.getPhoneNumber());
    }


    //4
    // 닉네임 유효성 검증 메서드
    private void validateNickname(User user, String newNickname) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 닉네임 변경 후 30일이 안 지났는지 확인
        if (user.getUpdatedAt() != null && user.getUpdatedAt().isAfter(thirtyDaysAgo)) {
            throw new IllegalArgumentException("닉네임을 변경한 지 30일이 지나지 않아 변경할 수 없습니다.");
        }

        // 닉네임이 이미 존재하는 경우
        Optional<User> userOptional = userRepository.findByUserNickname(newNickname);
        if (userOptional.isPresent() && !newNickname.equals(user.getUserNickname())) {
            throw new IllegalArgumentException("해당 닉네임이 이미 존재합니다.");
        }

        // 전과 동일한 닉네임
        if (newNickname.equalsIgnoreCase(user.getUserNickname())) {
            throw new IllegalArgumentException("이전과 동일한 닉네임입니다.");
        }

        // 닉네임이 2글자 이하인 경우
        if (newNickname.length() < 2) {
            throw new IllegalArgumentException("닉네임은 2자 이상이어야 합니다.");
        }

        // 닉네임이 10자 이상인 경우
        if (newNickname.length() > 10) {
            throw new IllegalArgumentException("닉네임은 10자 이하여야 합니다.");
        }

        // 닉네임이 유효한 경우 업데이트
        user.setUserNickname(newNickname);
        user.setUpdatedAt(LocalDateTime.now());
    }

    //5
    // 전화번호 유효성 검증 메서드
    private void validatePhoneNum(User user, String newPhoneNum) {
        // 핸드폰 값이 없거나, 변경됨을 확인
        if(user.getPhoneNumber() == null || !newPhoneNum.equals(user.getPhoneNumber())) {
            // 핸드폰 값이 숫자로 11개만 이루어진 형식임을 확인
            if (newPhoneNum.matches("\\d{11}")) {
                user.setPhoneNumber(newPhoneNum);
            } else {
                throw new IllegalArgumentException("전화번호는 숫자로 11자로만 입력되어야 합니다.");
            }
        }
    }



    // 유저가 즐겨찾기한 레스토랑 리스트들 반환
    public List<FavoriteRestaurantInfoDTO> getUserFavoriteRestaurantList(Integer userId) {
        User user = findUserById(userId);
        List<RestaurantFavoriteEntity> favoriteList = user.getRestaurantFavoriteEntityList();

        List<FavoriteRestaurantInfoDTO> favoriteRestaurantInfoDTOs = favoriteList.stream()
                .map(restaurantFavorite -> new FavoriteRestaurantInfoDTO(
                        restaurantFavorite.getRestaurant().getRestaurantName(),
                        restaurantFavorite.getRestaurant().getRestaurantId(),
                        restaurantFavorite.getRestaurant().getRestaurantImgUrl(),
                        restaurantFavorite.getRestaurant().getMainTier(),
                        restaurantFavorite.getRestaurant().getRestaurantCuisine(),
                        restaurantFavorite.getRestaurant().getRestaurantPosition()
                ))
                .collect(Collectors.toList());

        return favoriteRestaurantInfoDTOs;
    }


    //6
    // 유저가 평가한 레스토랑 리스트들 반환
    public List<EvaluatedRestaurantInfoDTO> getUserEvaluateRestaurantList(Integer userId) {
        User user = findUserById(userId);
        List<Evaluation> evaluationList = user.getEvaluationList();

        // 평가 리스트를 최신순으로 정렬
        evaluationList.sort((e1, e2) -> {
            LocalDateTime time1 = (e1.getUpdatedAt() != null && e1.getUpdatedAt().isAfter(e1.getCreatedAt())) ? e1.getUpdatedAt() : e1.getCreatedAt();
            LocalDateTime time2 = (e2.getUpdatedAt() != null && e2.getUpdatedAt().isAfter(e2.getCreatedAt())) ? e2.getUpdatedAt() : e2.getCreatedAt();
            return time2.compareTo(time1); // 내림차순 정렬
        });

        // 평가 리스트를 DTO로 변환하여 반환
        List<EvaluatedRestaurantInfoDTO> evaluateRestaurantInfoDTOS = evaluationList.stream()
                .map(evaluation -> {
                    String userCommentBody = evaluation.getCommentBody();

                    // EvaluationItemScoreList 에서 각 상황 이름을 추출
                    List<String> situationNames = evaluation.getEvaluationItemScoreList().stream()
                            .map(item -> item.getSituationEntity().getSituationName())
                            .collect(Collectors.toList());

                    return new EvaluatedRestaurantInfoDTO(
                            evaluation.getRestaurant().getRestaurantName(),
                            evaluation.getRestaurant().getRestaurantId(),
                            evaluation.getRestaurant().getRestaurantImgUrl(),
                            evaluation.getRestaurant().getRestaurantCuisine(),
                            evaluation.getEvaluationScore(),
                            userCommentBody,
                            situationNames
                    );
                })
                .collect(Collectors.toList());

        return evaluateRestaurantInfoDTOS;
    }


    //7
    // 유저가 작성한 커뮤니티 게시글 리스트들 반환
    public List<MypagePostDTO> getWrittenUserPosts(Integer userId) {
        List<Post> activePosts = postRepository.findActivePostsByUserId(userId);

        return activePosts.stream()
                .map(post -> new MypagePostDTO(
                        post.getPostId(),
                        post.getPostCategory(),
                        post.getPostTitle(),
                        post.getPostPhotoList().isEmpty() ? null : post.getPostPhotoList().get(0).getPhotoImgUrl(),
                        post.getPostBody().length() > 20 ? post.getPostBody().substring(0, 20) : post.getPostBody(),
                        post.getLikeCount(),
                        post.getPostCommentList().size(),
                        post.calculateTimeAgo()
                ))
                .collect(Collectors.toList());
    }


    //8
    // 유저가 스크랩한 커뮤니티 게시글 리스트들 반환
    public List<MypagePostDTO> getScrappedUserPosts(Integer userId) {
        List<PostScrap> scrappedPosts = postScrapRepository.findActiveScrappedPostsByUserId(userId);

        return scrappedPosts.stream()
                .map(scrap -> new MypagePostDTO(
                        scrap.getPost().getPostId(),
                        scrap.getPost().getPostCategory(),
                        scrap.getPost().getPostTitle(),
                        scrap.getPost().getPostPhotoList().isEmpty() ? null : scrap.getPost().getPostPhotoList().get(0).getPhotoImgUrl(),
                        scrap.getPost().getPostBody().length() > 20 ? scrap.getPost().getPostBody().substring(0, 20) : scrap.getPost().getPostBody(),
                        scrap.getPost().getLikeCount(),
                        scrap.getPost().getPostCommentList().size(),
                        scrap.getPost().calculateTimeAgo()
                ))
                .collect(Collectors.toList());
    }


    //9
    // 유저가 댓글단 커뮤니티 게시글 리스트들 반환
    public List<MypagePostCommentDTO> getCommentedUserPosts(Integer userId) {
        List<PostComment> commentedPosts = postCommentRepository.findActiveCommentedPostsByUserId(userId);

        // 데이터를 DTO 로 변환
        return commentedPosts.stream()
                .map(comment -> new MypagePostCommentDTO(
                        comment.getPost().getPostId(),
                        comment.getPost().getPostCategory(),
                        comment.getPost().getPostTitle(),
                        comment.getCommentBody().length() > 20 ? comment.getCommentBody().substring(0, 20) : comment.getCommentBody(),
                        comment.getLikeCount()
                ))
                .toList();
    }

    //10
    public List<NoticeDTO> getAllNotices() {
        return noticeRepo.findAll().stream()
                .map(notice -> new NoticeDTO(
                        notice.getNoticeTitle(),
                        notice.getNoticeHref(),
                        notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());
    }


}
