package com.kustaurant.kustaurant.global.apiUser.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginRequest {
    private String provider;
    private String providerId;
    private String naverAccessToken;
}