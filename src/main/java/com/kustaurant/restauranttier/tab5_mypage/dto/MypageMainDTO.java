package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MypageMainDTO {
    private String nickname;
    private int evaluationCount;
    private int favoriteCount;
}
