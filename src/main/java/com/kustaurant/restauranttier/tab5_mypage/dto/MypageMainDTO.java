package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageMainDTO {
    private String iconImgUrl;
    private String nickname;
    private int evaluationCount;
    private int PostCount;
}
