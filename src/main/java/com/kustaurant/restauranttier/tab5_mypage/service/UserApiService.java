package com.kustaurant.restauranttier.tab5_mypage.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.dto.*;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserApiService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    public User findUserById(Integer userid) {
        return userRepository.findById(userid).orElse(null);
    }


    // 마이페이지 화면에서 표시될 "유저닉네임, 좋아요맛집개수, 스크랩맛집개수" 를 반환.
    public MypageMainDTO getMypageInfo(Integer userid){
        User user = findUserById(userid);

        String userNickname = user.getUserNickname();
        int evalListSize = user.getEvaluationList().size();
        int favorListsize = user.getRestaurantFavoriteList().size();

        return new MypageMainDTO(userNickname, evalListSize, favorListsize);
    }



    // 마이페이지 프로필 정보(변경)화면에서 표시될 "닉네임, 메일주소, 핸드폰번호" 를 반환
    public ProfileDTO getProfileInfo(Integer userid){
        User user = findUserById(userid);

        String userNickname = user.getUserNickname();
        String userEmail = user.getUserEmail();
        String userPhoneNumber = user.getPhoneNumber();

        return new ProfileDTO(userNickname,userEmail,userPhoneNumber);
    }



    // 마이페이지 프로필 정보(변경)화면에서 로직을 검증하고 업데이트 하거나 결과를 반환
    public ProfileDTO updateUserProfile(Integer userid, ProfileDTO profileDTO) {
        User user = findUserById(userid);

        String receivedNickname = profileDTO.getNickname();
        String receivedPhoneNumber = profileDTO.getPhoneNumber();
        boolean updated = false;

        // 아무런 변경값 없이 프로필 저장하기 버튼을 누름
        if ((receivedPhoneNumber.isEmpty() || user.getPhoneNumber().equals(receivedPhoneNumber))
                && user.getUserNickname().equals(receivedNickname)) {
            throw new IllegalArgumentException("수정된 값이 없습니다.");
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

    // 전화번호 유효성 검증 메서드
    private void validatePhoneNum(User user, String newPhoneNum) {
        // 핸드폰 값이 변경됨을 확인
        if(!newPhoneNum.equals(user.getPhoneNumber())){
            // 핸드폰 값이 숫자로 11개만 이루어진 형식임을 확인
            if (newPhoneNum.matches("\\d{11}")) {
                user.setPhoneNumber(newPhoneNum);
            }else {
                throw new IllegalArgumentException("전화번호는 숫자로 11자로만 입력되어야 합니다.");
            }
        }
    }



    // 유저가 즐겨찾기한 레스토랑 리스트들 반환
    public List<FavoriteRestaurantInfoDTO> getUserFavoriteRestaurantList(Integer userId) {
        User user = findUserById(userId);
        List<RestaurantFavorite> favoriteList = user.getRestaurantFavoriteList();

        List<FavoriteRestaurantInfoDTO> favoriteRestaurantInfoDTOs = favoriteList.stream()
                .map(restaurantFavorite -> new FavoriteRestaurantInfoDTO(
                        restaurantFavorite.getRestaurant().getRestaurantName(),
                        restaurantFavorite.getRestaurant().getRestaurantImgUrl(),
                        restaurantFavorite.getRestaurant().getMainTier(),
                        restaurantFavorite.getRestaurant().getRestaurantCuisine(),
                        restaurantFavorite.getRestaurant().getRestaurantPosition()
                ))
                .collect(Collectors.toList());

        return favoriteRestaurantInfoDTOs;
    }



    // 유저가 평가한 레스토랑 리스트들 반환
    public List<EvaluateRestaurantInfoDTO> getUserEvaluateRestaurantList(Integer userId) {
        User user = findUserById(userId);
        List<Evaluation> evaluationList = user.getEvaluationList();

        // 평가 리스트를 최신순으로 정렬
        evaluationList.sort((e1, e2) -> {
            LocalDateTime time1 = (e1.getUpdatedAt() != null && e1.getUpdatedAt().isAfter(e1.getCreatedAt())) ? e1.getUpdatedAt() : e1.getCreatedAt();
            LocalDateTime time2 = (e2.getUpdatedAt() != null && e2.getUpdatedAt().isAfter(e2.getCreatedAt())) ? e2.getUpdatedAt() : e2.getCreatedAt();
            return time2.compareTo(time1); // 내림차순 정렬
        });

        // 평가 리스트를 DTO로 변환하여 반환
        List<EvaluateRestaurantInfoDTO> evaluateRestaurantInfoDTOS = evaluationList.stream()
                .map(evaluation -> new EvaluateRestaurantInfoDTO(
                            evaluation.getRestaurant().getRestaurantName(),
                            evaluation.getRestaurant().getRestaurantImgUrl(),
                            evaluation.getRestaurant().getRestaurantCuisine(),
                            evaluation.getEvaluationScore()
//                            evaluation.getEvaluationItemScoreList()
                    ))
                .collect(Collectors.toList());

        return evaluateRestaurantInfoDTOS;
    }



    // 유저가 작성한 커뮤니티 게시글 리스트들 반환
    public List<MypagePostDTO> getWrittenUserPosts(Integer userId){
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activePostFilter").setParameter("status", "active");

        User user = findUserById(userId);
        List<Post> activePosts = user.getPostList();

        // 데이터를 DTO로 변환
        List<MypagePostDTO> postDTOList = activePosts.stream()
                .map(post -> new MypagePostDTO(
                        post.getPostCategory(),
                        post.getPostTitle(),
                        post.getPostBody().length() > 20 ? post.getPostBody().substring(0, 20) : post.getPostBody(), // 최대 20자,
                        post.getLikeCount(),
                        post.getPostCommentList().size()
                ))
                .collect(Collectors.toList());

        return postDTOList;
    }



    // 유저가 스크랩한 커뮤니티 게시글 리스트들 반환
    public List<MypagePostDTO> getScrappedUserPosts(Integer userId) {
        User user = findUserById(userId);
        List<PostScrap> scrappedPosts = user.getScrapList();

        // 데이터를 DTO로 변환
        List<MypagePostDTO> postScrapsDTOList = scrappedPosts.stream()
                .map(scrap -> new MypagePostDTO(
                        scrap.getPost().getPostCategory(),
                        scrap.getPost().getPostTitle(),
                        scrap.getPost().getPostBody().length() > 20 ? scrap.getPost().getPostBody().substring(0, 20) : scrap.getPost().getPostBody(), // 최대 20자
                        scrap.getPost().getLikeCount(),
                        scrap.getPost().getPostCommentList().size()
                ))
                .collect(Collectors.toList());

        return postScrapsDTOList;
    }


    // 유저가 댓글단 커뮤니티 게시글 리스트들 반환
    public List<MypagePostCommentDTO> getCommentedUserPosts(Integer userId){
        User user = findUserById(userId);
        List<PostComment> commentedPosts = user.getPostCommentList();

        // 데이터를 DTO로 변환
        List<MypagePostCommentDTO> postCommentDTOList = commentedPosts.stream()
                .map(comment -> new MypagePostCommentDTO(
                        comment.getPost().getPostCategory(),
                        comment.getPost().getPostTitle(),
                        comment.getCommentBody().length() > 20 ? comment.getCommentBody().substring(0, 20) : comment.getCommentBody(),
                        comment.getLikeCount()
                ))
                .toList();

        return postCommentDTOList;
    }


}
