package com.kustaurant.restauranttier.tab3_tier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "evaluation data dto entity")
public class EvaluationDTO {
    @Schema(description = "메인 별점", example = "4.5")
    private Double evaluationScore;
    @Schema(description = "상황 리스트(한글 그대로)", example = "[\"혼밥\", \"2~4인\"]")
    private List<String> evaluationSituations;
    @Schema(description = "평가 이미지 url(이전 평가데이터를 불러올 때만 사용)")
    private String evaluationImgUrl;
    @Schema(description = "리뷰 멘트", example = "오 좀 맛있는데?")
    private String evaluationComment;
}
