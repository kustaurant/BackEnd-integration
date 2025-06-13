package com.kustaurant.kustaurant.user.controller.port;

import com.kustaurant.kustaurant.comment.domain.PostComment;
import com.kustaurant.kustaurant.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.post.domain.Post;
import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.user.controller.web.response.MypageDataDTO;

import java.util.List;

public interface MypageService {
    List<RestaurantFavorite> getRestaurantFavorites(Integer userId);
    List<EvaluationDomain> getEvaluations(Integer userId);
    List<Post> getActivePosts(Integer userId);
    List<PostComment> getActivePostComments(Integer userId);
    List<PostScrap> getPostScraps(Integer userId);

    MypageDataDTO getMypageData(Integer userId);
}
