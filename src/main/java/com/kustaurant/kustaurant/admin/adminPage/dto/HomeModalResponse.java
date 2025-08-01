package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HomeModalResponse {
    private Integer id;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private Boolean isActive; // 현재 시간 기준으로 활성화 여부
}