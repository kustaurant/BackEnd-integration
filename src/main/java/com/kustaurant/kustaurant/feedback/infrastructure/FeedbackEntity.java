package com.kustaurant.kustaurant.feedback.infrastructure;

import com.kustaurant.kustaurant.feedback.domain.Feedback;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="feedbacks_tbl")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comment;

    @Column(name = "user_id", nullable = false)
    private Long writer;

    private LocalDateTime createdAt;

    public static FeedbackEntity from(Long userId, Feedback feedback) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.id = feedback.getId();
        feedbackEntity.comment = feedback.getComment();
        feedbackEntity.writer = userId;
        feedbackEntity.createdAt = feedback.getCreatedAt();

        return feedbackEntity;
    }

    public Feedback toModel() {
        return Feedback.builder()
                .writer(writer)
                .comment(comment)
                .createdAt(createdAt)
                .build();
    }
}
