package com.kustaurant.mainapp.global.auth.argumentResolver;

import com.kustaurant.mainapp.user.user.domain.UserRole;

public record AuthUserInfo(
        Long id,
        UserRole role
) {
}
