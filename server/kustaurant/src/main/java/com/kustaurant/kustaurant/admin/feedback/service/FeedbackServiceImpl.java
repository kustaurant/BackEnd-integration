package com.kustaurant.kustaurant.admin.feedback.service;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.admin.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.admin.feedback.domain.Feedback;
import com.kustaurant.kustaurant.admin.feedback.service.port.FeedbackRepository;
import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final ClockHolder clockHolder;

    @Override
    public void create(Long userId, FeedbackRequest req) {
        Feedback feedback=Feedback.from(userId, req, clockHolder);
        feedbackRepository.save(feedback);
    }
}
