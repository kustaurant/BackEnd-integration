package com.kustaurant.kustaurant.feedback.service.port;

import com.kustaurant.kustaurant.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.feedback.domain.Feedback;

import java.util.List;

public interface FeedbackRepository {
    List<Feedback> findAllByUserId(Long userId);
    void save(Feedback feedback);
}
