package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;

public record AuthUserInfo(
        Long id,
        UserRole role
) {
}
