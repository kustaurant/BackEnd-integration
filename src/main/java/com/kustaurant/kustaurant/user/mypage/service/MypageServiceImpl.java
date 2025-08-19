package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageApiService;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.web.MypageDataView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 웹 부분은 마이페이지가 모든 정보를 한번에 넘겨주도록 되어있음
 */

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final EvaluationRepository evaluationRepository;
    private final PostRepository postRepository;

    private final MypageApiService mypageApiService;

    public List<RestaurantFavorite> getRestaurantFavorites(Long userId) {
        return restaurantFavoriteRepository.findSortedFavoritesByUserId(userId);
    }

    public List<Evaluation> getEvaluations(Long userId) {
        return new ArrayList<>(evaluationRepository.findSortedEvaluationByUserIdDesc(userId));
    }

    public List<Post> getActivePosts(Long userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public MypageDataView getMypageData(Long userId) {
        List<MyPostCommentResponse> postCommentDTO = mypageApiService.getCommentedUserPosts(userId);
        List<MyPostsResponse> postScrapDTO = mypageApiService.getScrappedUserPosts(userId);

        return MypageDataView.builder()
                .restaurantFavoriteList(getRestaurantFavorites(userId))
                .restaurantEvaluationList(getEvaluations(userId))
                .postList(getActivePosts(userId))
                .postCommentList(postCommentDTO)
                .postScrapList(postScrapDTO)
                .build();
    }
}
