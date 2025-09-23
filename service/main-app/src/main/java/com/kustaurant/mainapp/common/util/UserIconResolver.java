package com.kustaurant.mainapp.common.util;

import com.kustaurant.mainapp.user.user.domain.UserLevel;

public final class UserIconResolver {
    private UserIconResolver() {}

    public static String resolve(int evaluationCnt) {
        return UserLevel.of(evaluationCnt).iconPath();
    }
    public static String resolve(long evaluationCnt) {
        return UserLevel.of(evaluationCnt).iconPath();
    }
}