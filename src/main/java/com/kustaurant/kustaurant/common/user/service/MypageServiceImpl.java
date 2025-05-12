package com.kustaurant.kustaurant.common.user.service;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.common.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.service.port.PostRepository;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.user.controller.port.MypageService;
import com.kustaurant.kustaurant.common.user.controller.web.response.MypageDataDTO;
import com.kustaurant.kustaurant.common.user.controller.web.response.MypageWebPostCommentDTO;
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

    public List<RestaurantFavorite> getRestaurantFavorites(Integer userId) {
        return restaurantFavoriteRepository.findSortedFavoritesByUserId(userId);
    }

    public List<EvaluationDomain> getEvaluations(Integer userId) {
        return new ArrayList<>(evaluationRepository.findSortedEvaluationByUserIdDesc(userId));
    }

    public List<Post> getActivePosts(Integer userId) {
        return postRepository.findActiveByUserId(userId);
    }


    public List<PostComment> getActivePostComments(Integer userId) {
        return postCommentRepository.findActiveByUserId(userId);
    }

    public List<PostScrap> getPostScraps(Integer userId) {
        return postScrapRepository.findByUserId(userId);
    }

    private List<MypageWebPostCommentDTO> convertCommentsToDTOs(List<PostComment> postComments) {
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
                .map(comment -> new MypageWebPostCommentDTO(
                        comment.getCommentId(),
                        comment.getCommentBody(),
                        comment.getNetLikes(),
                        comment.calculateTimeAgo(),
                        postIdToTitle.getOrDefault(comment.getPostId(), "알 수 없음"),
                        comment.getPostId()
                ))
                .toList();
    }

    @Override
    public MypageDataDTO getMypageData(Integer userId) {
        List<PostComment> comments = getActivePostComments(userId);
        List<MypageWebPostCommentDTO> postCommentDTO = convertCommentsToDTOs(comments);

        return MypageDataDTO.builder()
                .restaurantFavoriteList(getRestaurantFavorites(userId))
                .restaurantEvaluationList(getEvaluations(userId))
                .postList(getActivePosts(userId))
                .postCommentList(postCommentDTO)
                .postScrapList(getPostScraps(userId))
                .build();
    }
}
