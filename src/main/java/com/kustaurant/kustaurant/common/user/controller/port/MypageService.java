package com.kustaurant.kustaurant.common.user.controller.port;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.user.controller.web.response.MypageDataDTO;

import java.util.List;

public interface MypageService {
    List<RestaurantFavorite> getRestaurantFavorites(Integer userId);
    List<EvaluationDomain> getEvaluations(Integer userId);
    List<Post> getActivePosts(Integer userId);
    List<PostComment> getActivePostComments(Integer userId);
    List<PostScrap> getPostScraps(Integer userId);

    MypageDataDTO getMypageData(Integer userId);
}
