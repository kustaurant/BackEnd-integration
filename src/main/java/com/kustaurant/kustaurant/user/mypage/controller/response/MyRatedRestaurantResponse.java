package com.kustaurant.kustaurant.user.mypage.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MyRatedRestaurantResponse (
        Integer restaurantId,
        String restaurantName,
        String restaurantImgURL,
        String cuisine,
        Double evaluationScore,
        String evaluationBody,
        @Schema(description = "유저가 선택한 상황카테고리들", example = "(string으로된 배열입니다. 혼밥, 소개팅 등등")
        List<String> evaluationItemScores
){}
