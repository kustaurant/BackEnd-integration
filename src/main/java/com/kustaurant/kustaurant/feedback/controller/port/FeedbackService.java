package com.kustaurant.kustaurant.feedback.controller.port;

import com.kustaurant.kustaurant.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.feedback.domain.Feedback;

public interface FeedbackService {

    void create(Long userId, FeedbackRequest feedbackCreate);
}
