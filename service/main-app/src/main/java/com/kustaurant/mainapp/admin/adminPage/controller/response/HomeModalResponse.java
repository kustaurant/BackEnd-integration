package com.kustaurant.mainapp.admin.adminPage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record HomeModalResponse(
        Integer id,
        String title,
        String body,
        LocalDateTime createdAt,
        LocalDateTime expiredAt,
        Boolean isActive // 현재 시간 기준으로 활성화 여
) { }