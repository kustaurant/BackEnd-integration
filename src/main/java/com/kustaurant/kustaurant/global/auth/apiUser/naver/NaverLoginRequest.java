package com.kustaurant.kustaurant.global.auth.apiUser.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginRequest {
    private String provider;
    private String providerId;
    private String naverAccessToken;
}