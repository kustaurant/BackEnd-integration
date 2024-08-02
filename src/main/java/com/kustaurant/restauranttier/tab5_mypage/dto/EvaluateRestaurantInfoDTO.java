package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class EvaluateRestaurantInfoDTO {
    private String restaurantName;
    private String restaurantImgURL;
    private String cuisine;
    private Double evaluationScore;
//    @Schema(description = "유저가 선택한 상황카테고리들 입니다.", example = "한식, 혼밥 등등")
//    private List<EvaluationItemScore> evaluationItemScores;
}
