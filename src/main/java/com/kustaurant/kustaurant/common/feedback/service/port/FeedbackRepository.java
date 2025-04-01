package com.kustaurant.kustaurant.common.feedback.service.port;

import com.kustaurant.kustaurant.common.feedback.domain.Feedback;

import java.util.List;

public interface FeedbackRepository {
    List<Feedback> findAllByUserId(Integer userId);
    Feedback save(Feedback feedback);
}
