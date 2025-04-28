package com.kustaurant.kustaurant.common.restaurant.application.service.command.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantComment;
import com.kustaurant.kustaurant.common.restaurant.application.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "evaluation data dto entity")
public class RestaurantCommentDTO {
    @Schema(description = "코멘트 id", example = "3")
    private Integer commentId;
    @Schema(description = "평가 별점", example = "4.5")
    private Double commentScore;
    @Schema(description = "아이콘 이미지 url")
    private String commentIconImgUrl;
    @Schema(description = "닉네임", example = "역병")
    private String commentNickname;
    @Schema(description = "시간 정보", example = "2분전")
    private String commentTime;
    @Schema(description = "평가 이미지 url")
    private String commentImgUrl;
    @Schema(description = "평가 멘트", example = "오 좀 맛있는데?")
    private String commentBody;
    @Schema(description = "유저의 추천 비추천 여부 (1=추천 누름, 0=아무것도 안누름, -1=비추천 누름)", example = "1")
    private Integer commentLikeStatus;
    @Schema(description = "추천 개수", example = "14")
    private Integer commentLikeCount;
    @Schema(description = "비추천 개수", example = "3")
    private Integer commentDislikeCount;
    @Schema(description = "사용자가 단 댓글인지 여부", example = "true")
    private Boolean isCommentMine;
    @Schema(description = "대댓글 리스트")
    private List<RestaurantCommentDTO> commentReplies;
    @JsonIgnore
    private LocalDateTime date;
    @JsonIgnore
    private UserEntity UserEntity;
    @JsonIgnore
    private EvaluationEntity evaluation;

    public static RestaurantCommentDTO convertCommentWhenEvaluation(EvaluationEntity evaluation, UserEntity UserEntity, String userAgent) {
        return new RestaurantCommentDTO(
                evaluation.getEvaluationId() + EvaluationConstants.EVALUATION_ID_OFFSET,
                evaluation.getEvaluationScore(),
                RestaurantConstants.getIconImgUrl(evaluation.getUser(), userAgent),
                evaluation.getUser().getNickname().getValue(),
                evaluation.calculateTimeAgo(),
                evaluation.getCommentImgUrl(),
                evaluation.getCommentBody(),
                isUserLikeDisLikeStatus(evaluation, UserEntity),
                evaluation.getRestaurantCommentLikeList().size(),
                evaluation.getRestaurantCommentDislikeList().size(),
                isCommentMine(UserEntity, evaluation),
                null,
                evaluation.getUpdatedAt() == null ? evaluation.getCreatedAt() : evaluation.getUpdatedAt(),
                evaluation.getUser(),
                evaluation
        );
    }

    public static RestaurantCommentDTO convertCommentWhenSubComment(RestaurantComment comment, Double evaluationScore, UserEntity UserEntity, String userAgent) {
        return new RestaurantCommentDTO(
                comment.getCommentId(),
                evaluationScore,
                RestaurantConstants.getIconImgUrl(comment.getUser(), userAgent),
                comment.getUser().getNickname().getValue(),
                comment.calculateTimeAgo(),
                null,
                comment.getCommentBody(),
                isUserLikeDisLikeStatus(comment, UserEntity),
                comment.getRestaurantCommentLikeList().size(),
                comment.getRestaurantCommentDislikeList().size(),
                isCommentMine(UserEntity, comment),
                null,
                comment.getUpdatedAt() == null ? comment.getCreatedAt() : comment.getUpdatedAt(),
                comment.getUser(),
                null
        );
    }

    // 추천수와 비추천 수만 반환하는 생성자
    public static RestaurantCommentDTO convertCommentWhenLikeDislike(RestaurantComment comment, UserEntity UserEntity) {
        return new RestaurantCommentDTO(
                null, null, null, null, null, null, null,
                isUserLikeDisLikeStatus(comment, UserEntity),
                comment.getRestaurantCommentLikeList().size(),
                comment.getRestaurantCommentDislikeList().size(), null, null, null, null, null
        );
    }
    public static RestaurantCommentDTO convertCommentWhenLikeDislike(EvaluationEntity evaluation, UserEntity UserEntity) {
        return new RestaurantCommentDTO(
                null, null, null, null, null, null, null,
                isUserLikeDisLikeStatus(evaluation, UserEntity),
                evaluation.getRestaurantCommentLikeList().size(),
                evaluation.getRestaurantCommentDislikeList().size(), null, null, null, null, null
        );
    }

    public int commentLikeDiffDislike() {
        return commentLikeCount - commentDislikeCount;
    }


    public static boolean isCommentMine(UserEntity UserEntity, EvaluationEntity evaluation) {
        if (UserEntity == null) {
            return false;
        }
        return evaluation.getUser().equals(UserEntity);
    }

    public static boolean isCommentMine(UserEntity UserEntity, RestaurantComment comment) {
        if (UserEntity == null) {
            return false;
        }
        return comment.getUser().equals(UserEntity);
    }

    private static int isUserLikeDisLikeStatus(EvaluationEntity evaluation, UserEntity UserEntity) {
        if (UserEntity == null) {
            return 0;
        }
        // 유저가 좋아요를 눌렀는지 확인
        boolean liked = evaluation.getRestaurantCommentLikeList().stream()
                .anyMatch(like -> like.getUser().equals(UserEntity));

        if (liked) {
            return 1;
        }

        // 유저가 싫어요를 눌렀는지 확인
        boolean disliked = evaluation.getRestaurantCommentDislikeList().stream()
                .anyMatch(dislike -> dislike.getUser().equals(UserEntity));

        return disliked ? -1 : 0;
    }

    private static int isUserLikeDisLikeStatus(RestaurantComment comment, UserEntity UserEntity) {
        if (UserEntity == null) {
            return 0;
        }
        // 유저가 좋아요를 눌렀는지 확인
        boolean liked = comment.getRestaurantCommentLikeList().stream()
                .anyMatch(like -> like.getUser().equals(UserEntity));

        if (liked) {
            return 1;
        }

        // 유저가 싫어요를 눌렀는지 확인
        boolean disliked = comment.getRestaurantCommentDislikeList().stream()
                .anyMatch(dislike -> dislike.getUser().equals(UserEntity));

        return disliked ? -1 : 0;
    }
}
