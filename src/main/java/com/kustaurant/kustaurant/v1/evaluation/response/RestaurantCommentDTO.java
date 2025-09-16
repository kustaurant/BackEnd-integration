package com.kustaurant.kustaurant.v1.evaluation.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.review.ReviewsResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public static RestaurantCommentDTO fromV2(ReviewsResponse v2) {
        int likeStatus = v2.reactionType() == ReactionType.LIKE ? 1 : (v2.reactionType() == ReactionType.DISLIKE ? -1 : 0);
        return new RestaurantCommentDTO(
                (int) (long) v2.evalId(),
                v2.evalScore(),
                v2.writerIconImgUrl(),
                v2.writerNickname(),
                v2.timeAgo(),
                v2.evalImgUrl(),
                v2.evalBody(),
                likeStatus,
                v2.evalLikeCount(),
                v2.evalDislikeCount(),
                v2.isEvaluationMine(),
                v2.evalCommentList().stream().map(RestaurantCommentDTO::fromV2).toList()
        );
    }

    public static RestaurantCommentDTO fromV2(EvalCommentResponse v2) {
        int likeStatus = v2.reactionType() == ReactionType.LIKE ? 1 : (v2.reactionType() == ReactionType.DISLIKE ? -1 : 0);
        return new RestaurantCommentDTO(
                (int) (long) v2.commentId(),
                0.0,
                v2.writerIconImgUrl(),
                v2.writerNickname(),
                v2.timeAgo(),
                null,
                v2.commentBody(),
                likeStatus,
                v2.commentLikeCount(),
                v2.commentDislikeCount(),
                v2.isCommentMine(),
                null
        );
    }
}
