package com.kustaurant.mainapp.v1.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EvaluatedRestaurantInfoDTO {
    private String restaurantName;
    private Integer restaurantId;
    private String restaurantImgURL;
    private String cuisine;
    private Double evaluationScore;
    private String restaurantComment;
    private List<String> evaluationItemScores;
}