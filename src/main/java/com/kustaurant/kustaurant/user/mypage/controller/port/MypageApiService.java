package com.kustaurant.kustaurant.user.mypage.controller.port;

import com.kustaurant.kustaurant.admin.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;

import java.util.List;

public interface MypageApiService {
    MypageMainResponse getMypageInfo(Long userId);

    ProfileResponse getProfile(Long userId);

    ProfileResponse updateUserProfile(Long userId, ProfileUpdateRequest req);

    List<MyRestaurantResponse> getUserFavoriteRestaurantList(Long userId);

    List<MyRatedRestaurantResponse> getUserEvaluateRestaurantList(Long userId);

    List<MyPostsResponse> getUserPosts(Long userId);

    List<MyPostsResponse> getScrappedUserPosts(Long userId);

    List<MyPostCommentResponse> getCommentedUserPosts(Long userId);

    List<NoticeDTO> getAllNotices();
}
