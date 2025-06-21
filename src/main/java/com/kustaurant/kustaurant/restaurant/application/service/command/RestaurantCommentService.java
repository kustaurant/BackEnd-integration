package com.kustaurant.kustaurant.restaurant.application.service.command;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.infrastructure.*;
import com.kustaurant.kustaurant.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.restaurant.application.service.command.dto.RestaurantCommentDTO;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.user.infrastructure.UserJpaRepository;
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
    private final UserJpaRepository userJpaRepository;

    public RestaurantComment findCommentByCommentId(Integer commentId) {
        Optional<RestaurantComment> commentOptional = restaurantCommentRepository.findByCommentIdAndStatus(commentId, "ACTIVE");
        if (commentOptional.isEmpty()) {
            throw new DataNotFoundException(RESTAURANT_COMMENT_NOT_FOUND, commentId, "식당 대댓글");
        }
        return commentOptional.get();
    }

    // 식당 대댓글 삭제
    @Transactional
    public void deleteComment(RestaurantComment comment, UserEntity UserEntity) {
        if (comment == null || UserEntity == null) {
            return;
        }

        if (!comment.getUser().equals(UserEntity)) {
            throw new ParamException("해당 유저가 단 대댓글이 아닙니다.");
        }

        comment.setStatus("DELETED");
        comment.setCommentLikeCount(0);
        restaurantCommentRepository.save(comment);

        // 좋아요, 싫어요 삭제
        restaurantCommentLikeRepository.deleteAll(comment.getRestaurantCommentLikeList());
        restaurantCommentDislikeRepository.deleteAll(comment.getRestaurantCommentDislikeList());
    }

    public List<RestaurantCommentDTO> getRestaurantCommentList(Integer restaurantId, Integer userId, boolean sortPopular, String userAgent) {
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
                        userId,
                        userAgent
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
                                        .map(comment -> RestaurantCommentDTO.convertCommentWhenSubComment(comment, mainComment.getCommentScore(), userId, userAgent))
                                        .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                                        .collect(Collectors.toList())
                        ))
                .toList();
    }

    public RestaurantCommentDTO getRestaurantCommentDTO(int evaluationId, Integer userId, String userAgent) {
        EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
        if (evaluation != null) {
            RestaurantCommentDTO restaurantCommentDTO = RestaurantCommentDTO.convertCommentWhenEvaluation(evaluation, userId, userAgent);
            restaurantCommentDTO.setCommentReplies(
                    restaurantCommentDTO.getEvaluation().getRestaurantCommentList().stream()
                            .filter(comment -> comment.getStatus().equals("ACTIVE"))
                            .map(comment -> RestaurantCommentDTO.convertCommentWhenSubComment(comment, restaurantCommentDTO.getCommentScore(), userId, userAgent))
                            .sorted(Comparator.comparing(RestaurantCommentDTO::getDate))
                            .collect(Collectors.toList())
            );
            return restaurantCommentDTO;
        } else {
            throw new DataNotFoundException(EVALUATION_NOT_FOUND, evaluationId, "평가");
        }
    }

    @Transactional
    public void addComment(Integer restaurantId, Integer userId, String commentBody) {
        RestaurantComment restaurantComment = new RestaurantComment();

        RestaurantEntity restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        if (userId != null) {
            UserEntity userEntity = userJpaRepository.getReferenceById((long) userId);

            restaurantComment.setUser(userEntity);
            restaurantComment.setRestaurant(restaurant);
            restaurantComment.setCommentBody(commentBody);
            restaurantComment.setStatus("ACTIVE");
            restaurantComment.setCreatedAt(LocalDateTime.now());

            restaurantCommentRepository.save(restaurantComment);
        }
    }

    public RestaurantComment addSubComment(RestaurantEntity restaurant, UserEntity UserEntity, String commentBody, EvaluationEntity evaluation) {
        RestaurantComment restaurantComment = new RestaurantComment();

        restaurantComment.setUser(UserEntity);
        restaurantComment.setRestaurant(restaurant);
        restaurantComment.setEvaluation(evaluation);
        restaurantComment.setCommentBody(commentBody);
        restaurantComment.setStatus("ACTIVE");
        restaurantComment.setCreatedAt(LocalDateTime.now());
        restaurantComment.setCommentLikeCount(0);

        restaurantCommentRepository.save(restaurantComment);

        return restaurantComment;
    }

    public boolean isUserLikedComment(Integer userId, Integer commentId) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, commentId);
        return restaurantCommentlikeOptional.isPresent();
    }

    public boolean isUserHatedComment(UserEntity UserEntity, RestaurantComment restaurantComment) {
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(UserEntity.getUserId(), restaurantComment.getCommentId());
        return restaurantCommentdislikeOptional.isPresent();
    }

    @Transactional
    public void likeComment(Integer userId, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());

        UserEntity userEntity = userJpaRepository.getReferenceById((long) userId);
        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "unliked");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(userEntity, restaurantComment);
            restaurantCommentLikeRepository.save(restaurantCommentlike);
            commentLikeCountAdd(restaurantComment, 2);
            responseMap.put("status", "switched");
        } else { // 새로 좋아요를 누르는 경우
            RestaurantCommentLike restaurantCommentlike = new RestaurantCommentLike(userEntity, restaurantComment);
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
    public void dislikeComment(Integer userId, RestaurantComment restaurantComment, Map<String, String> responseMap) {
        Optional<RestaurantCommentLike> restaurantCommentlikeOptional = restaurantCommentLikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());
        Optional<RestaurantCommentDislike> restaurantCommentdislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndRestaurantCommentId(userId, restaurantComment.getCommentId());

        UserEntity userEntity = userJpaRepository.getReferenceById((long) userId);
        if (restaurantCommentlikeOptional.isPresent() && restaurantCommentdislikeOptional.isPresent()) {
            throw new IllegalStateException(restaurantComment.getRestaurant().getRestaurantId() + "id 식당 싫어요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (restaurantCommentdislikeOptional.isPresent()) { // 싫어요를 눌렀었던 경우
            restaurantCommentDislikeRepository.delete(restaurantCommentdislikeOptional.get());
            commentLikeCountAdd(restaurantComment, 1);
            responseMap.put("status", "unhated");
        } else if (restaurantCommentlikeOptional.isPresent()) { // 좋아요를 눌렀었던 경우
            restaurantCommentLikeRepository.delete(restaurantCommentlikeOptional.get());
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(userEntity, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -2);
            responseMap.put("status", "switched");
        } else { // 새로 싫어요를 누르는 경우
            RestaurantCommentDislike restaurantCommentdislike = new RestaurantCommentDislike(userEntity, restaurantComment);
            restaurantCommentDislikeRepository.save(restaurantCommentdislike);
            commentLikeCountAdd(restaurantComment, -1);
            responseMap.put("status", "hated");
        }
    }
}
