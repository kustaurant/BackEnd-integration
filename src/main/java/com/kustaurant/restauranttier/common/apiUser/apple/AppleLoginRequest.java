package com.kustaurant.restauranttier.common.apiUser.apple;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleLoginRequest {
    private String provider;
    private String identityToken;
    private String authorizationCode;
}
