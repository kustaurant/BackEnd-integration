package com.kustaurant.kustaurant.feedback.infrastructure;

import com.kustaurant.kustaurant.feedback.domain.Feedback;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="feedbacks_tbl")
public class FeedbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedbackId;

    private String feedbackContent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDateTime createdAt;
    private String status;

//    public static FeedbackEntity from(Feedback feedback) {
//        FeedbackEntity feedbackEntity = new FeedbackEntity();
//        feedbackEntity.id=feedback.ge
//    }

    public Feedback toModel() {
        return Feedback.builder()
                .writer(user.toModel())
                .comment(feedbackContent)
                .createdAt(createdAt)
                .build();
    }
}
