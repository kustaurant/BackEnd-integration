package com.kustaurant.kustaurant.evaluation.comment.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.RestaurantCommentDislikeRepository;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.RestaurantCommentLikeRepository;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.RestaurantCommentRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class EvaluationCommentService {
    private final RestaurantCommentRepository restaurantCommentRepository;
    private final RestaurantCommentLikeRepository restaurantCommentLikeRepository;
    private final RestaurantCommentDislikeRepository restaurantCommentDislikeRepository;
    private final RestaurantRepository restaurantRepository;
    private final EvaluationService evaluationService;

    public RestaurantCommentEntity findCommentByCommentId(Integer commentId) {
        Optional<RestaurantCommentEntity> commentOptional = restaurantCommentRepository.findByCommentIdAndStatus(commentId, "ACTIVE");
        if (commentOptional.isEmpty()) {
            throw new DataNotFoundException(RESTAURANT_COMMENT_NOT_FOUND, commentId, "식당 대댓글");
        }
        return commentOptional.get();
    }

    // 평가 대댓글 삭제
    @Transactional
    public void deleteComment(RestaurantCommentEntity comment, Long userId) {
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

//    public List<EvalCommResponse> getRestaurantCommentList(Integer restaurantId, Long userId, boolean sortPopular) {
//        // 평가 데이터 및 댓글 가져오기
//        List<EvaluationEntity> evaluations = evaluationService.findByRestaurantId(restaurantId);
//        List<EvalCommResponse> mainCommentList = new ArrayList<>(evaluations.stream()
//                .filter(evaluation -> {
//                    String body = evaluation.getCommentBody();
//                    String imgUrl = evaluation.getCommentImgUrl();
//                    return (body != null && !body.isEmpty()) || (imgUrl != null && !imgUrl.isEmpty());
//                })
//                .map(evaluation -> EvalCommResponse.convertCommentWhenEvaluation(
//                        evaluation,
//                        userId
//                ))
//                .toList());
//
//        // 정렬
//        if (sortPopular) {
//            mainCommentList.sort(Comparator.comparing(EvalCommResponse::commentLikeDiffDislike).reversed());
//        } else {
//            // TODO: 평가 정렬을 UpdateAt을 반영해야됨
//            mainCommentList.sort(Comparator.comparing(EvalCommResponse::getDate).reversed());
//        }
//
//        // 각 댓글에 대댓글 추가해서 반환
//        return mainCommentList.stream()
//                .peek(mainComment ->
//                        mainComment.setCommentReplies(
//                                mainComment.getEvaluation().getRestaurantCommentList().stream()
//                                        .filter(comment -> comment.getStatus().equals("ACTIVE"))
//                                        .map(comment -> EvalCommResponse.convertCommentWhenSubComment(comment, mainComment.getCommentScore(), userId))
//                                        .sorted(Comparator.comparing(EvalCommResponse::getDate))
//                                        .collect(Collectors.toList())
//                        ))
//                .toList();
//    }
//
//    public EvalCommResponse getRestaurantCommentDTO(int evaluationId, Long userId) {
//        EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
//        if (evaluation != null) {
//            EvalCommResponse restaurantCommentDTO = EvalCommResponse.convertCommentWhenEvaluation(evaluation, userId);
//            restaurantCommentDTO.setCommentReplies(
//                    restaurantCommentDTO.getEvaluation().getRestaurantCommentList().stream()
//                            .filter(comment -> comment.getStatus().equals("ACTIVE"))
//                            .map(comment -> EvalCommResponse.convertCommentWhenSubComment(comment, restaurantCommentDTO.getCommentScore(), userId))
//                            .sorted(Comparator.comparing(EvalCommResponse::getDate))
//                            .collect(Collectors.toList())
//            );
//            return restaurantCommentDTO;
//        } else {
//            throw new DataNotFoundException(EVALUATION_NOT_FOUND, evaluationId, "평가");
//        }
//    }

    public String addComment(Integer restaurantId, Long userId, String commentBody) {
        RestaurantCommentEntity restaurantComment = new RestaurantCommentEntity();

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

    public RestaurantCommentEntity addSubComment(RestaurantEntity restaurant, Long userId, String commentBody, EvaluationEntity evaluation) {
        RestaurantCommentEntity restaurantComment = new RestaurantCommentEntity();

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

    public boolean isUserLikedComment(Long userId, RestaurantCommentEntity restaurantComment) {
        Optional<RestaurantCommentLikeEntity> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        return restaurantCommentlikeOptional.isPresent();
    }

    public boolean isUserHatedComment(Long userId, RestaurantCommentEntity restaurantComment) {
        Optional<RestaurantCommentDislikeEntity> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        return restaurantCommentdislikeOptional.isPresent();
    }

    @Transactional
    public void likeComment(Long userId, RestaurantCommentEntity restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLikeEntity> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        Optional<RestaurantCommentDislikeEntity> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            RestaurantCommentLikeEntity restaurantCommentlike = new RestaurantCommentLikeEntity(userId, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 2);
            responseMap.put("status", "switched");
        } else { // 새로 좋아요를 누르는 경우
            RestaurantCommentLikeEntity restaurantCommentlike = new RestaurantCommentLikeEntity(userId, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "liked");
        }
    }

    private void commentLikeCountAdd(RestaurantCommentEntity restaurantComment, int addNum) {
        restaurantComment.setCommentLikeCount(restaurantComment.getCommentLikeCount() + addNum);
        restaurantCommentRepository.save(restaurantComment);
    }

    @Transactional
    public void dislikeComment(Long userId, RestaurantCommentEntity restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLikeEntity> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        Optional<RestaurantCommentDislikeEntity> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());

        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 싫어요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "unhated");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            RestaurantCommentDislikeEntity restaurantCommentdislike = new RestaurantCommentDislikeEntity(userId, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -2);
            responseMap.put("status", "switched");
        } else { // 새로 싫어요를 누르는 경우
            RestaurantCommentDislikeEntity restaurantCommentdislike = new RestaurantCommentDislikeEntity(userId, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "hated");
        }
    }
}
