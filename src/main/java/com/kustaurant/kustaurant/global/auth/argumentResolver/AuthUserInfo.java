package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.user.domain.enums.UserRole;

public record AuthUserInfo(
        Integer id,
        UserRole role
) {
}
