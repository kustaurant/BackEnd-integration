package com.kustaurant.kustaurant.evaluation.comment.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import io.swagger.v3.oas.annotations.media.Schema;

public record EvalCommentResponse(
        @Schema(description = "코멘트 id", example = "3")
        Long commentId,
        @Schema(description = "유저 평가 등급에 따른 이미지 url")
        String writerIconImgUrl,
        @Schema(description = "닉네임", example = "역병")
        String writerNickname,
        @Schema(description = "시간 정보", example = "2분전")
        String timeAgo,
        @Schema(description = "코멘트", example = "저두 이 의견에 동의합니당")
        String commentBody,
        @Schema(description = "유저의 추천 비추천 여부 (null= 아무것도 안누름)", example = "LIKE")
        ReactionType reactionType,
        @Schema(description = "추천 개수", example = "14")
        Integer commentLikeCount,
        @Schema(description = "비추천 개수", example = "3")
        Integer commentDislikeCount,
        @Schema(description = "사용자가 단 댓글인지 여부", example = "true")
        Boolean isCommentMine
) {
    public static EvalCommentResponse from(
            EvalComment evalComment,
            User writer,
            ReactionType reaction,
            Long currentUserId
    ) {
        return new EvalCommentResponse(
                evalComment.getId(),
                UserIconResolver.resolve(writer.getEvalCount()),
                writer.getNickname().getValue(),
                TimeAgoUtil.toKor(evalComment.getCreatedAt()),
                evalComment.getBody(),
                reaction,
                evalComment.getLikeCount(),
                evalComment.getDislikeCount(),
                currentUserId.equals(evalComment.getUserId())
        );
    }
}
