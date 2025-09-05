package com.kustaurant.kustaurant.admin.adminPage.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record HomeModalUpdateRequest(
        String title,
        String body,
        LocalDateTime expiredAt
) {}