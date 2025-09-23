package com.kustaurant.mainapp.v1.evaluation.response;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.common.util.TimeAgoResolver;
import com.kustaurant.mainapp.common.util.UserIconResolver;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;
import com.kustaurant.mainapp.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.mainapp.evaluation.review.ReviewsResponse;
import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.v1.evaluation.EvaluationV1Controller;
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
        return new RestaurantCommentDTO(
                (int) (long) v2.evalId() + EvaluationV1Controller.EVALUATION_ID_OFFSET,
                v2.evalScore(),
                v2.writerIconImgUrl(),
                v2.writerNickname(),
                v2.timeAgo(),
                v2.evalImgUrl(),
                v2.evalBody(),
                getStatus(v2.reactionType()),
                v2.evalLikeCount(),
                v2.evalDislikeCount(),
                v2.isEvaluationMine(),
                v2.evalCommentList().stream().map(RestaurantCommentDTO::fromV2).toList()
        );
    }

    public static RestaurantCommentDTO fromV2(EvalCommentResponse v2) {
        return new RestaurantCommentDTO(
                (int) (long) v2.commentId(),
                0.0,
                v2.writerIconImgUrl(),
                v2.writerNickname(),
                v2.timeAgo(),
                null,
                v2.commentBody(),
                getStatus(v2.reactionType()),
                v2.commentLikeCount(),
                v2.commentDislikeCount(),
                v2.isCommentMine(),
                null
        );
    }

    public static RestaurantCommentDTO fromV2(EvalReactionResponse v2) {
        return new RestaurantCommentDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                getStatus(v2.reaction()),
                v2.likeCount(),
                v2.dislikeCount(),
                null,
                null
        );
    }

    public static RestaurantCommentDTO fromV2(EvalCommentReactionResponse v2) {
        return new RestaurantCommentDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                getStatus(v2.reaction()),
                v2.likeCount(),
                v2.dislikeCount(),
                null,
                null
        );
    }

    public static RestaurantCommentDTO fromV2(EvalComment v2, User user) {
        return new RestaurantCommentDTO(
                (int) (long) v2.getId(),
                null,
                UserIconResolver.resolve(user.getEvalCount()),
                user.getNickname().toString(),
                TimeAgoResolver.toKor(v2.getCreatedAt()),
                null,
                v2.getBody(),
                0,
                v2.getLikeCount(),
                v2.getDislikeCount(),
                true,
                null
        );
    }

    private static int getStatus(ReactionType reactionType) {
        return reactionType == ReactionType.LIKE ? 1 : (reactionType == ReactionType.DISLIKE ? -1 : 0);
    }
}
