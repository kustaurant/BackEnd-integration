package com.kustaurant.kustaurant.feedback.domain;

import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.global.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Feedback {
    private final User writer;
    private final String comment;
    private final LocalDateTime createdAt;

    @Builder
    public Feedback(User writer, String comment, LocalDateTime createdAt) {
        this.writer = writer;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static Feedback from(User writer, FeedbackCreate feedbackCreate, ClockHolder clockHolder) {
        return Feedback.builder()
                .comment(feedbackCreate.getComment())
                .writer(writer)
                .createdAt(clockHolder.now())
                .build();
    }
}
