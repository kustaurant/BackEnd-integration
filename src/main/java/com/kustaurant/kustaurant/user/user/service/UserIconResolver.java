package com.kustaurant.kustaurant.user.user.service;

import com.kustaurant.kustaurant.user.user.domain.enums.UserLevel;

public final class UserIconResolver {
    private UserIconResolver() {}

    public static String resolve(int evaluationCnt) {
        return UserLevel.of(evaluationCnt).iconPath();
    }
}