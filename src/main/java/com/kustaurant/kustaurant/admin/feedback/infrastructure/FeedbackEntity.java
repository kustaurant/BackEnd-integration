package com.kustaurant.kustaurant.admin.feedback.infrastructure;

import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="admin_feedback")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer id;

    private String comment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static FeedbackEntity from(Long userId, Feedback feedback) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.id = feedback.getId();
        feedbackEntity.comment = feedback.getComment();
        feedbackEntity.userId = userId;

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
