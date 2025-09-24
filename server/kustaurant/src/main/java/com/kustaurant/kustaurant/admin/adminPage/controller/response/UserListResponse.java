package com.kustaurant.kustaurant.admin.adminPage.controller.response;

import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;

import java.time.LocalDateTime;

public record UserListResponse(
        Long userId,
        String nickname,
        LoginApi loginApi,
        String status,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt
) { }