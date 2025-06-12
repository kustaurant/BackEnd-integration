package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.common.user.domain.enums.UserRole;

public record AuthUserInfo(
        Integer id,
        UserRole role
) {
}
