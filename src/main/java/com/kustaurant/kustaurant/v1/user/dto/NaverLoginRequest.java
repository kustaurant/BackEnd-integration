package com.kustaurant.kustaurant.v1.user.dto;

import lombok.Data;

@Data
public class NaverLoginRequest {
    private String provider;
    private String providerId;
    private String naverAccessToken;
}