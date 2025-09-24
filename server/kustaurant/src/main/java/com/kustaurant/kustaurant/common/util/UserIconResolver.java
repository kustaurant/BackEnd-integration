package com.kustaurant.kustaurant.common.util;

import com.kustaurant.kustaurant.user.user.domain.UserLevel;

public final class UserIconResolver {
    private UserIconResolver() {}

    public static String resolve(int evaluationCnt) {
        return UserLevel.of(evaluationCnt).iconPath();
    }
    public static String resolve(long evaluationCnt) {
        return UserLevel.of(evaluationCnt).iconPath();
    }
}