package com.kustaurant.kustaurant.evaluation.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.user.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewsResponse(
        @Schema(description = "코멘트 id", example = "3")
        Long evalId,
        @Schema(description = "평가 별점", example = "4.5")
        Double evalScore,
        @Schema(description = "유저 평가 등급에 따른 이미지 url")
        String writerIconImgUrl,
        @Schema(description = "닉네임", example = "역병")
        String writerNickname,
        @Schema(description = "시간 정보", example = "2분전")
        String timeAgo,
        @Schema(description = "평가에 유저가 첨부한 이미지 url")
        String evalImgUrl,
        @Schema(description = "평가 멘트", example = "오 좀 맛있는데?")
        String evalBody,
        @Schema(description = "유저의 추천 비추천 여부 (null= 아무것도 안누름)", example = "LIKE")
        ReactionType reactionType,
        @Schema(description = "추천 개수", example = "14")
        Integer evalLikeCount,
        @Schema(description = "비추천 개수", example = "3")
        Integer evalDislikeCount,
        @Schema(description = "사용자가 단 댓글인지 여부", example = "true")
        Boolean isCommentMine,
        @Schema(description = "대댓글 리스트")
        List<EvalCommentResponse> evalCommentList,
        @JsonIgnore
        User user,
        @JsonIgnore
        Evaluation evaluation
) { }
