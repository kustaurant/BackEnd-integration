package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.user.user.domain.UserRole;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record AuthUserInfo(
        Long id,
        UserRole role
) {
}
