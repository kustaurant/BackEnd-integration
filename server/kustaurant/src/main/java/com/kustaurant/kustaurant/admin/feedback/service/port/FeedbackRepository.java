package com.kustaurant.kustaurant.admin.feedback.service.port;

import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;

import java.util.List;

public interface FeedbackRepository {
    List<Feedback> findAllByUserId(Long userId);
    void save(Feedback feedback);
}
