package com.kustaurant.restauranttier.tab3_tier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "evaluation data dto entity")
public class RestaurantCommentDTO {
    @Schema(description = "평가 id", example = "3")
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
    @Schema(description = "추천 비추천 여부 (1=추천 누름, 0=아무것도 안누름, -1=비추천 누름)", example = "1")
    private Integer commentLikeStatus;
    @Schema(description = "추천 개수", example = "14")
    private Integer commentLikeCount;
    @Schema(description = "비추천 개수", example = "3")
    private Integer commentDislikeCount;
    @Schema(description = "대댓글 리스트")
    private List<RestaurantCommentDTO> commentReplies;
}
