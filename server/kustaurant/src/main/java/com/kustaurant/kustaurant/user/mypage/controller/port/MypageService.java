package com.kustaurant.kustaurant.user.mypage.controller.port;

import com.kustaurant.kustaurant.admin.notice.domain.Notice;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;
import com.kustaurant.kustaurant.user.mypage.controller.response.web.MypageDataView;

import java.util.List;

public interface MypageService {

    ProfileResponse getProfile(Long userId);

    ProfileUpdateResponse updateUserProfile(Long userId, ProfileUpdateRequest req);

    List<MyRestaurantResponse> getUserFavoriteRestaurantList(Long userId);

    List<MyRatedRestaurantResponse> getUserEvaluateRestaurantList(Long userId);

    List<MyPostsResponse> getUserPosts(Long userId);

    List<MyPostsResponse> getScrappedUserPosts(Long userId);

    List<MyPostCommentResponse> getCommentedUserPosts(Long userId);

    List<Notice> getAllNotices();
    MypageDataView getMypageWebData(Long userId);
}
