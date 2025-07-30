package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {
    private Long userId;
    private String nickname;
    private String loginApi;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}