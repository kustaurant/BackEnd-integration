package com.kustaurant.kustaurant.feedback.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackEntity, Long> {
    List<FeedbackEntity> findAllByUser_UserId(Long id);
}
