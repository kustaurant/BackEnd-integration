package com.kustaurant.kustaurant.admin.feedback.infrastructure;

import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;
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
    @Column(name = "feedback_id")
    private Integer id;

    private String comment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private LocalDateTime createdAt;

    public static FeedbackEntity from(Long userId, Feedback feedback) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.id = feedback.getId();
        feedbackEntity.comment = feedback.getComment();
        feedbackEntity.userId = userId;
        feedbackEntity.createdAt = feedback.getCreatedAt();

        return feedbackEntity;
    }

    public Feedback toModel() {
        return Feedback.builder()
                .writer(userId)
                .comment(comment)
                .createdAt(createdAt)
                .build();
    }
}
