package com.kustaurant.kustaurant.user.mypage.controller.response.web;

import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRatedRestaurantResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MypageDataView {
    private List<MyRestaurantResponse> restaurantFavoriteList;
    private List<MyRatedRestaurantResponse> restaurantEvaluationList;
    private List<MyPostsResponse> postList;
    private List<MyPostCommentResponse> postCommentList;
    private List<MyPostsResponse> postScrapList;
}
