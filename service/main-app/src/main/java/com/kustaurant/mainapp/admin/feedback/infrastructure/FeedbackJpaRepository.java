package com.kustaurant.mainapp.admin.feedback.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackEntity, Long> {
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    List<FeedbackEntity> findAllByUserId(Long id);
}
