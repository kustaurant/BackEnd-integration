package com.kustaurant.mainapp.admin.feedback.controller.port;

import com.kustaurant.mainapp.admin.feedback.controller.Request.FeedbackRequest;

public interface FeedbackService {

    void create(Long userId, FeedbackRequest feedbackCreate);
}
