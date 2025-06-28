package com.kustaurant.kustaurant.admin.feedback.controller.port;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;

public interface FeedbackService {

    void create(Long userId, FeedbackRequest feedbackCreate);
}
