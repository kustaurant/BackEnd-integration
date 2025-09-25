package com.kustaurant.kustaurant.admin.adminPage.controller.response;

import java.time.LocalDateTime;

public record FeedbackListResponse (
        Integer feedbackId,
        String comment,
        Long userId,
        String userNickname,
        LocalDateTime createdAt
) {}