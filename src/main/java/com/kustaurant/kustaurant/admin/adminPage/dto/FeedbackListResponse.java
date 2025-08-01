package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackListResponse {
    private Integer feedbackId;
    private String comment;
    private Long userId;
    private String userNickname;
    private LocalDateTime createdAt;
}