package com.kustaurant.kustaurant.admin.feedback.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackEntity, Long> {
    List<FeedbackEntity> findAllByUserId(Long id);
}
