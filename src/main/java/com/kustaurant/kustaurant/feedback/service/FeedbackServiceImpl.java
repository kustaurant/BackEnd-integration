package com.kustaurant.kustaurant.feedback.service;

import com.kustaurant.kustaurant.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.feedback.domain.Feedback;
import com.kustaurant.kustaurant.feedback.service.port.FeedbackRepository;
import com.kustaurant.kustaurant.common.service.port.ClockHolder;
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
