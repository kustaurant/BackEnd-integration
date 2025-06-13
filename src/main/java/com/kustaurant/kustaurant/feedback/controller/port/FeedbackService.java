package com.kustaurant.kustaurant.feedback.controller.port;

import com.kustaurant.kustaurant.feedback.domain.Feedback;
import com.kustaurant.kustaurant.feedback.domain.FeedbackCreate;

public interface FeedbackService {

    Feedback create(FeedbackCreate feedbackCreate, Integer userId);
}
