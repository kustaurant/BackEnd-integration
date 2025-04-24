package com.kustaurant.kustaurant.common.user.service;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.comment.infrastructure.OPostCommentRepository;
import com.kustaurant.kustaurant.common.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.common.notice.infrastructure.NoticeRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.user.controller.api.response.*;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MypageApiService {

    private final OUserRepository OUserRepository;
    private final PostRepository postRepository;
    private final OPostScrapRepository postScrapRepository;
    private final OPostCommentRepository postCommentRepository;
    private final NoticeRepository noticeRepo;

    public UserEntity findUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return OUserRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }

    //1
    // 마이페이지 화면에서 표시될 "유저닉네임, 좋아요맛집 갯수, 내 게시글 갯수" 를 반환.
    public MypageMainDTO getMypageInfo(Integer userid, String userAgent){
        UserEntity UserEntity = findUserById(userid);

        String iconURL = RestaurantConstants.getIconImgUrl(UserEntity, userAgent);
        String userNickname = UserEntity.getNickname().getValue();
        int evalListSize = UserEntity.getEvaluationList().size();
        int commuPostListSize = UserEntity.getPostList().size();

        return new MypageMainDTO(iconURL, userNickname, evalListSize, commuPostListSize);
    }


    //2
    // 마이페이지 프로필 정보(변경)화면에서 표시될 "닉네임, 메일주소, 핸드폰번호" 를 반환
    public ProfileDTO getProfileInfo(Integer userid){
        UserEntity UserEntity = findUserById(userid);

        String userNickname = UserEntity.getNickname().getValue();
        String userEmail = UserEntity.getEmail();
        String userPhoneNumber = UserEntity.getPhoneNumber().getValue();

        return new ProfileDTO(userNickname,userEmail,userPhoneNumber);
    }


    //3
    // 마이페이지 프로필 정보(변경)화면에서 로직을 검증하고 업데이트 하거나 결과를 반환
    public ProfileDTO updateUserProfile(Integer userId, ProfileDTO profileDTO) {
        UserEntity userEntity = findUserById(userId);

        String receivedNicknameStr = profileDTO.getNickname();
        String receivedPhoneNumberStr = profileDTO.getPhoneNumber();
        boolean updated = false;

        Nickname receivedNickname = new Nickname(receivedNicknameStr);
        PhoneNumber receivedPhoneNumber = new PhoneNumber(receivedPhoneNumberStr);

        // 변경값이 없음
        if ((receivedPhoneNumberStr == null || receivedPhoneNumberStr.isEmpty() ||
                (userEntity.getPhoneNumber() != null && userEntity.getPhoneNumber().equals(receivedPhoneNumber)))
                && userEntity.getNickname().equals(receivedNickname)) {
            throw new IllegalArgumentException("변경된 값이 없습니다.");
        }

        // 전화번호가 변경됨
        if (!userEntity.getPhoneNumber().equals(receivedPhoneNumber)) {
            validatePhoneNum(userEntity, receivedPhoneNumber);
            updated = true;
        }

        // 닉네임이 변경됨
        if (!userEntity.getNickname().equals(receivedNickname)) {
            validateNickname(userEntity, receivedNickname);
            updated = true;
        }

        if (!updated) {
            throw new IllegalArgumentException("프로필 변경에 실패하였습니다!");
        }

        OUserRepository.save(userEntity);
        return new ProfileDTO(
                userEntity.getNickname().getValue(),
                userEntity.getEmail(),
                userEntity.getPhoneNumber().getValue()
        );
    }



    //4
    // 닉네임 유효성 검증 메서드
    private void validateNickname(UserEntity UserEntity, Nickname newNickname) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 닉네임 변경 후 30일이 안 지났는지 확인
        if (UserEntity.getUpdatedAt() != null && UserEntity.getUpdatedAt().isAfter(thirtyDaysAgo)) {
            throw new IllegalArgumentException("닉네임을 변경한 지 30일이 지나지 않아 변경할 수 없습니다.");
        }

        // 닉네임이 이미 존재하는 경우
        Optional<UserEntity> userOptional = OUserRepository.findByNickname_Value(newNickname.getValue());
        if (userOptional.isPresent() && !newNickname.equals(UserEntity.getNickname())) {
            throw new IllegalArgumentException("해당 닉네임이 이미 존재합니다.");
        }

        // 닉네임이 유효한 경우 업데이트
        UserEntity.setNickname(newNickname);
        UserEntity.setUpdatedAt(LocalDateTime.now());
    }

    //5
    // 전화번호 유효성 검증 메서드
    private void validatePhoneNum(UserEntity userEntity, PhoneNumber newPhoneNumber) {
        if (userEntity.getPhoneNumber() == null || !userEntity.getPhoneNumber().equals(newPhoneNumber)) {
            userEntity.setPhoneNumber(newPhoneNumber);
        }
    }




    // 유저가 즐겨찾기한 레스토랑 리스트들 반환
    public List<FavoriteRestaurantInfoDTO> getUserFavoriteRestaurantList(Integer userId) {
        UserEntity UserEntity = findUserById(userId);
        List<RestaurantFavoriteEntity> favoriteList = UserEntity.getRestaurantFavoriteList();

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
        UserEntity UserEntity = findUserById(userId);
        List<EvaluationEntity> evaluationList = UserEntity.getEvaluationList();

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
                    List<String> situationNames = evaluation.getEvaluationSituationEntityList().stream()
                            .map(item -> item.getSituation().getSituationName())
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
        List<PostEntity> activePosts = postRepository.findActivePostsByUserId(userId);

        return activePosts.stream()
                .map(post -> new MypagePostDTO(
                        post.getPostId(),
                        post.getPostCategory(),
                        post.getPostTitle(),
                        post.getPostPhotoList().isEmpty() ? null : post.getPostPhotoList().get(0).getPhotoImgUrl(),
                        post.getPostBody().length() > 20 ? post.getPostBody().substring(0, 20) : post.getPostBody(),
                        post.getLikeCount(),
                        post.getPostCommentList().size(),
                        post.toDomain().calculateTimeAgo()
                ))
                .collect(Collectors.toList());
    }


    //8
    // 유저가 스크랩한 커뮤니티 게시글 리스트들 반환
    public List<MypagePostDTO> getScrappedUserPosts(Integer userId) {
        List<PostScrapEntity> scrappedPosts = postScrapRepository.findActiveScrappedPostsByUserId(userId);

        return scrappedPosts.stream()
                .map(scrap -> new MypagePostDTO(
                        scrap.getPost().getPostId(),
                        scrap.getPost().getPostCategory(),
                        scrap.getPost().getPostTitle(),
                        scrap.getPost().getPostPhotoList().isEmpty() ? null : scrap.getPost().getPostPhotoList().get(0).getPhotoImgUrl(),
                        scrap.getPost().getPostBody().length() > 20 ? scrap.getPost().getPostBody().substring(0, 20) : scrap.getPost().getPostBody(),
                        scrap.getPost().getLikeCount(),
                        scrap.getPost().getPostCommentList().size(),
                        scrap.getPost().toDomain().calculateTimeAgo()
                ))
                .collect(Collectors.toList());
    }


    //9
    // 유저가 댓글단 커뮤니티 게시글 리스트들 반환
    public List<MypagePostCommentDTO> getCommentedUserPosts(Integer userId) {
        List<PostCommentEntity> commentedPosts = postCommentRepository.findActiveCommentedPostsByUserId(userId);

        // 데이터를 DTO 로 변환
        return commentedPosts.stream()
                .map(comment -> new MypagePostCommentDTO(
                        comment.getPost().getPostId(),
                        comment.getPost().getPostCategory(),
                        comment.getPost().getPostTitle(),
                        comment.getCommentBody().length() > 20 ? comment.getCommentBody().substring(0, 20) : comment.getCommentBody(),
                        comment.getLikeCount(),
                        comment.calculateTimeAgo()
                ))
                .toList();
    }

    //10
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
