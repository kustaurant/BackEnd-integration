package com.kustaurant.kustaurant.user.mypage.controller.port;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import com.kustaurant.kustaurant.user.mypage.controller.response.web.MypageDataView;

import java.util.List;

public interface MypageService {
    List<RestaurantFavorite> getRestaurantFavorites(Long userId);
    List<Evaluation> getEvaluations(Long userId);
    List<Post> getActivePosts(Long userId);
    List<PostComment> getActivePostComments(Long userId);
    List<PostScrap> getPostScraps(Long userId);

    MypageDataView getMypageData(Long userId);
}
