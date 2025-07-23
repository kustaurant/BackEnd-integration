package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MypageDataView {
    private List<RestaurantFavorite> restaurantFavoriteList;
    private List<Evaluation> restaurantEvaluationList;
    private List<Post> postList;
    private List<PostCommentView> postCommentList;
    private List<ScrappedPostView> postScrapList;
}
