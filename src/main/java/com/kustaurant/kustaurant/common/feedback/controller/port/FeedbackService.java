package com.kustaurant.kustaurant.common.feedback.controller.port;

import com.kustaurant.kustaurant.common.feedback.domain.Feedback;
import com.kustaurant.kustaurant.common.feedback.domain.FeedbackCreate;

public interface FeedbackService {

    Feedback create(FeedbackCreate feedbackCreate, Integer userId);
}
