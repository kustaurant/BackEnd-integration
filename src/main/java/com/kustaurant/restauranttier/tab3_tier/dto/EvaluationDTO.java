package com.kustaurant.restauranttier.tab3_tier.dto;

import com.kustaurant.restauranttier.tab3_tier.constants.RestaurantConstants;
import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Schema(description = "evaluation data dto entity")
public class EvaluationDTO {
    @Schema(description = "메인 별점", example = "4.5")
    private Double evaluationScore;
    @Schema(description = "상황 리스트(정수로)\n\n(1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)", example = "[1,2,7]")
    private List<Integer> evaluationSituations;
    @Schema(description = "평가 이미지 url(이전 평가데이터를 불러올 때만 사용)")
    private String evaluationImgUrl;
    @Schema(description = "리뷰 멘트", example = "오 좀 맛있는데?")
    private String evaluationComment;
    @Schema(description = "별점 멘트", example = "")
    private List<RestaurantConstants.StarComment> starComments;

    public static EvaluationDTO convertEvaluation(Evaluation evaluation, RestaurantComment comment) {
        return new EvaluationDTO(
                // TODO: 여기 수정
                evaluation.getEvaluationScore(),
                evaluation.getEvaluationItemScoreList().stream().map(evaluationItemScore -> evaluationItemScore.getSituation().getSituationId()).collect(Collectors.toList()),
                comment == null ? null : comment.getCommentImgUrl(),
                comment == null ? null : comment.getCommentBody(),
                RestaurantConstants.STAR_COMMENTS
        );
    }

    public static EvaluationDTO convertEvaluationWhenNoEvaluation() {
        return new EvaluationDTO(
                null, null, null, null, RestaurantConstants.STAR_COMMENTS
        );
    }
}
