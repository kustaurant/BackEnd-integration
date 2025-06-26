package com.kustaurant.kustaurant.user.mypage.service;

import com.kustaurant.kustaurant.comment.domain.PostComment;
import com.kustaurant.kustaurant.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.post.domain.Post;
import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.response.MypageDataView;
import com.kustaurant.kustaurant.user.mypage.controller.response.PostCommentView;
import com.kustaurant.kustaurant.user.mypage.controller.response.ScrappedPostView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final EvaluationRepository evaluationRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostScrapRepository postScrapRepository;

    public List<RestaurantFavorite> getRestaurantFavorites(Long userId) {
        return restaurantFavoriteRepository.findSortedFavoritesByUserId(userId);
    }

    public List<EvaluationDomain> getEvaluations(Long userId) {
        return new ArrayList<>(evaluationRepository.findSortedEvaluationByUserIdDesc(userId));
    }

    public List<Post> getActivePosts(Long userId) {
        return postRepository.findActiveByUserId(userId);
    }


    public List<PostComment> getActivePostComments(Long userId) {
        return postCommentRepository.findActiveByUserId(userId);
    }

    public List<PostScrap> getPostScraps(Long userId) {
        return postScrapRepository.findByUserId(userId);
    }

    private List<PostCommentView> convertCommentsToDTOs(List<PostComment> postComments) {
        List<Integer> postIds = postComments.stream()
                .map(PostComment::getPostId)
                .distinct()
                .toList();

        Map<Integer, String> postIdToTitle = postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        Post::getTitle
                ));

        return postComments.stream()
                .map(comment -> new PostCommentView(
                        comment.getCommentId(),
                        comment.getCommentBody(),
                        comment.getNetLikes(),
                        comment.calculateTimeAgo(),
                        postIdToTitle.getOrDefault(comment.getPostId(), "알 수 없음"),
                        comment.getPostId()
                ))
                .toList();
    }

    private List<ScrappedPostView> getScrapViews(Long userId) {
        return postScrapRepository.findScrapViewsByUserId(userId);
    }

    @Override
    public MypageDataView getMypageData(Long userId) {
        List<PostComment> comments = getActivePostComments(userId);
        List<PostCommentView> postCommentDTO = convertCommentsToDTOs(comments);
        List<ScrappedPostView> postScrapDTO = getScrapViews(userId);

        return MypageDataView.builder()
                .restaurantFavoriteList(getRestaurantFavorites(userId))
                .restaurantEvaluationList(getEvaluations(userId))
                .postList(getActivePosts(userId))
                .postCommentList(postCommentDTO)
                .postScrapList(postScrapDTO)
                .build();
    }
}
