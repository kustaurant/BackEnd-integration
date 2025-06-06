package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.common.user.domain.UserRole;

public record AuthUserInfo(
        Integer id,
        UserRole role
) {
}
