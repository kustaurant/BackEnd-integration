package com.kustaurant.restauranttier.common.apiUser.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginRequest {
    private String provider;
    private String providerId;
    private String naverAccessToken;
}