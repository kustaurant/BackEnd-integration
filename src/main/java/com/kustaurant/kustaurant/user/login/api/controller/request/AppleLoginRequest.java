package com.kustaurant.kustaurant.user.login.api.controller.request;

import jakarta.validation.constraints.NotBlank;

public record AppleLoginRequest (
        @NotBlank String identityToken,
        @NotBlank String authorizationCode
){}
