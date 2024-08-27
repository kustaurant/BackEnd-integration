package com.kustaurant.restauranttier.tab5_mypage.dto;

import com.kustaurant.restauranttier.tab3_tier.entity.EvaluationItemScore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor

public class EvaluatedRestaurantInfoDTO {
    private String restaurantName;
    private String restaurantImgURL;
    private String cuisine;
    private Double evaluationScore;
    private String restaurantComment;
    @Schema(description = "유저가 선택한 상황카테고리들 입니다.", example = "(string으로된 배열입니다. 혼밥, 소개팅 등등")
    private List<String> evaluationItemScores;
}
