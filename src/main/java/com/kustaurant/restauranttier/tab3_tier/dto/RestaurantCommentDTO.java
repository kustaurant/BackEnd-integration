package com.kustaurant.restauranttier.tab3_tier.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    @Schema(description = "대댓글 리스트")
    private List<RestaurantCommentDTO> commentReplies;
    @JsonIgnore
    private LocalDateTime date;

    public static RestaurantCommentDTO convertEvaluationAndComment(RestaurantComment comment, Double evaluationScore, User user) {
        return new RestaurantCommentDTO(
                comment.getCommentId(),
                evaluationScore,
                comment.getCommentImgUrl(),
                comment.getUser().getUserNickname(),
                comment.calculateTimeAgo(),
                comment.getCommentImgUrl(),
                comment.getCommentBody(),
                isUserLikeDisLike(comment, user),
                comment.getRestaurantCommentlikeList().size(),
                comment.getRestaurantCommentdislikeList().size(),
                null,
                comment.getUpdatedAt() == null ? comment.getCreatedAt() : comment.getUpdatedAt()
        );
    }

    private static int isUserLikeDisLike(RestaurantComment comment, User user) {
        if (user == null) {
            return 0;
        }
        // 유저가 좋아요를 눌렀는지 확인
        boolean liked = comment.getRestaurantCommentlikeList().stream()
                .anyMatch(like -> like.getUser().equals(user));

        if (liked) {
            return 1;
        }

        // 유저가 싫어요를 눌렀는지 확인
        boolean disliked = comment.getRestaurantCommentdislikeList().stream()
                .anyMatch(dislike -> dislike.getUser().equals(user));

        return disliked ? -1 : 0;
    }
}