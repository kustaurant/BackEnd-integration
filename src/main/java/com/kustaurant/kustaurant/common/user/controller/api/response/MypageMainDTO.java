package com.kustaurant.kustaurant.common.user.controller.api.response;

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
