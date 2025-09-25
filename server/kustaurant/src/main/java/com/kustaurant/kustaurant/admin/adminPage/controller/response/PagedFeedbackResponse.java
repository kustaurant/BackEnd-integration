package com.kustaurant.kustaurant.admin.adminPage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record PagedFeedbackResponse(
        List<FeedbackListResponse> feedbacks,
        Long totalElements,
        Integer totalPages,
        Integer currentPage,
        Integer pageSize,
        Boolean hasNext,
        Boolean hasPrevious
) {}