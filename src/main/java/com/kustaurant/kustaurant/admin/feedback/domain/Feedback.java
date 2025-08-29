package com.kustaurant.kustaurant.admin.feedback.domain;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Feedback {
    private final Integer id;
    private final Long writer;
    private final String comment;
    private final LocalDateTime createdAt;

    public static Feedback from(
            Long writer,
            FeedbackRequest req,
            ClockHolder clockHolder
    ) {
        return Feedback.builder()
                .comment(req.comment())
                .writer(writer)
                .createdAt(clockHolder.now())
                .build();
    }
}
