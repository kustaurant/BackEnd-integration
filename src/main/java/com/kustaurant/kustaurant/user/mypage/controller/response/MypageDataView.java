package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MypageDataView {
    private List<RestaurantFavorite> restaurantFavoriteList;
    private List<EvaluationDomain> restaurantEvaluationList;
    private List<Post> postList;
    private List<PostCommentView> postCommentList;
    private List<ScrappedPostView> postScrapList;
}
