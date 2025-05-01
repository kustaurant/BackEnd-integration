package com.kustaurant.kustaurant.common.user.controller.web.response;

import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MypageDataDTO {
    private List<RestaurantFavorite> restaurantFavoriteList;
    private List<EvaluationDomain> restaurantEvaluationList;
    private List<Post> postList;
    private List<MypageWebPostCommentDTO> postCommentList;
    private List<PostScrap> postScrapList;
}
