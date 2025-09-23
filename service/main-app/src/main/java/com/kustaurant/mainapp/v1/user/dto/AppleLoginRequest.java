package com.kustaurant.mainapp.v1.user.dto;

import lombok.Data;

@Data
public class AppleLoginRequest {
    private String provider;
    private String identityToken;
    private String authorizationCode;
}
