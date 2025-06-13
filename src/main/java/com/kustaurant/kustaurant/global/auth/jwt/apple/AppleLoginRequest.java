package com.kustaurant.kustaurant.global.auth.jwt.apple;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public record AppleLoginRequest (
        @NotBlank String provider,
        @NotBlank String identityToken,
        @NotBlank String authorizationCode
){}
