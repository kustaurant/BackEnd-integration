package com.kustaurant.kustaurant.restaurant.application.service.command;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.infrastructure.*;
import com.kustaurant.kustaurant.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.restaurant.application.service.command.dto.RestaurantCommentDTO;
import com.kustaurant.kustaurant.user.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RestaurantCommentService {
    private final RestaurantCommentRepository restaurantCommentRepository;
    private final RestaurantCommentLikeRepository restaurantCommentLikeRepository;
    private final RestaurantCommentDislikeRepository restaurantCommentDislikeRepository;
    private final RestaurantRepository restaurantRepository;
    private final EvaluationService evaluationService;

    public RestaurantComment findCommentByCommentId(Integer commentId) {
        Optional<RestaurantComment> commentOptional = restaurantCommentRepository.findByCommentIdAndStatus(commentId, "ACTIVE");
        if (commentOptional.isEmpty()) {
            throw new DataNotFoundException(RESTAURANT_COMMENT_NOT_FOUND, commentId, "식당 대댓글");
        }
        return commentOptional.get();
    }

    // 식당 대댓글 삭제
    @Transactional
    public void deleteComment(RestaurantComment comment, Long userId) {
        if (comment == null || userId == null) {
            return;
        }

        if (!comment.getUserId().equals(userId)) {
            throw new ParamException("해당 유저가 단 대댓글이 아닙니다.");
        }

        comment.setStatus("DELETED");
        comment.setCommentLikeCount(0);
        restaurantCommentRepository.save(comment);

        // 좋아요, 싫어요 삭제
        restaurantCommentLikeRepository.deleteAll(comment.getRestaurantCommentLikeList());
        restaurantCommentDislikeRepository.deleteAll(comment.getRestaurantCommentDislikeList());
    }

    public List<RestaurantCommentDTO> getRestaurantCommentList(Integer restaurantId, Long userId, boolean sortPopular) {
        // 평가 데이터 및 댓글 가져오기
        List<EvaluationEntity> evaluations = evaluationService.findByRestaurantId(restaurantId);
        List<RestaurantCommentDTO> mainCommentList = new ArrayList<>(evaluations.stream()
                .filter(evaluation -> {
                    String body = evaluation.getCommentBody();
                    String imgUrl = evaluation.getCommentImgUrl();
                    return (body != null && !body.isEmpty()) || (imgUrl != null && !imgUrl.isEmpty());
                })
                .map(evaluation -> RestaurantCommentDTO.convertCommentWhenEvaluation(
                        evaluation,
                        userId
                ))
                .toList());

        // 정렬
        if (sortPopular) {
            mainCommentList.sort(Comparator.comparing(RestaurantCommentDTO::commentLikeDiffDislike).reversed());
        } else {
            // TODO: 평가 정렬을 UpdateAt을 반영해야됨
            mainCommentList.sort(Comparator.comparing(RestaurantCommentDTO::getDate).reversed());
        }

        // 각 댓글에 대댓글 추가해서 반환
        return mainCommentList.stream()
                .peek(mainComment ->
                        mainComment.setCommentReplies(
                                mainComment.getEvaluation().getRestaurantCommentList().stream()
                                        .filter(comment -> comment.getStatus().equals("ACTIVE"))
                                        .map(comment -> RestaurantCommentDTO.convertCommentWhenSubComment(comment, mainComment.getCommentScore(), userId))
                                        .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                                        .collect(Collectors.toList())
                        ))
                .toList();
    }

    public RestaurantCommentDTO getRestaurantCommentDTO(int evaluationId, Long userId, String userAgent) {
        EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
        if (evaluation != null) {
            RestaurantCommentDTO restaurantCommentDTO = RestaurantCommentDTO.convertCommentWhenEvaluation(evaluation, userId);
            restaurantCommentDTO.setCommentReplies(
                    restaurantCommentDTO.getEvaluation().getRestaurantCommentList().stream()
                            .filter(comment -> comment.getStatus().equals("ACTIVE"))
                            .map(comment -> RestaurantCommentDTO.convertCommentWhenSubComment(comment, restaurantCommentDTO.getCommentScore(), userId))
                            .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                            .collect(Collectors.toList())
            );
            return restaurantCommentDTO;
        } else {
            throw new DataNotFoundException(EVALUATION_NOT_FOUND, evaluationId, "평가");
        }
    }

    public String addComment(Integer restaurantId, Long userId, String commentBody) {
        RestaurantComment restaurantComment = new RestaurantComment();

        RestaurantEntity restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        if (userId!=null) {
            restaurantComment.setUserId(userId);
            restaurantComment.setRestaurant(restaurant);
            restaurantComment.setCommentBody(commentBody);
            restaurantComment.setStatus("ACTIVE");
            restaurantComment.setCreatedAt(LocalDateTime.now());

            restaurantCommentRepository.save(restaurantComment);

            return "ok";
        } else {
            return "userTokenId";
        }
    }

    public RestaurantComment addSubComment(RestaurantEntity restaurant, Long userId, String commentBody, EvaluationEntity evaluation) {
        RestaurantComment restaurantComment = new RestaurantComment();

        restaurantComment.setUserId(userId);
        restaurantComment.setRestaurant(restaurant);
        restaurantComment.setEvaluation(evaluation);
        restaurantComment.setCommentBody(commentBody);
        restaurantComment.setStatus("ACTIVE");
        restaurantComment.setCreatedAt(LocalDateTime.now());
        restaurantComment.setCommentLikeCount(0);

        restaurantCommentRepository.save(restaurantComment);

        return restaurantComment;
    }

    public boolean isUserLikedComment(Long userId, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);
        return restaurantCommentlikeOptional.isPresent();
    }

    public boolean isUserHatedComment(Long userId, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);
        return restaurantCommentdislikeOptional.isPresent();
    }

    @Transactional
    public void likeComment(Long userId, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(userId, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 2);
            responseMap.put("status", "switched");
        } else { // 새로 좋아요를 누르는 경우
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(userId, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "liked");
        }
    }

    private void commentLikeCountAdd(RestaurantComment restaurantComment, int addNum) {
        restaurantComment.setCommentLikeCount(restaurantComment.getCommentLikeCount() + addNum);
        restaurantCommentRepository.save(restaurantComment);
    }

    @Transactional
    public void dislikeComment(Long userId, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserAndRestaurantComment(userId, restaurantComment);

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 싫어요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "unhated");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(userId, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -2);
            responseMap.put("status", "switched");
        } else { // 새로 싫어요를 누르는 경우
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(userId, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "hated");
        }
    }
}
