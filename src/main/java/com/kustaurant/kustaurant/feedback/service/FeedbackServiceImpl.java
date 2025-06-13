package com.kustaurant.kustaurant.feedback.service;

import com.kustaurant.kustaurant.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.feedback.domain.Feedback;
import com.kustaurant.kustaurant.feedback.domain.FeedbackCreate;
import com.kustaurant.kustaurant.feedback.service.port.FeedbackRepository;
import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    @Override
    public Feedback create(FeedbackCreate feedbackCreate, Integer userId) {
        User writer= userRepository.getById(userId);
        Feedback feedback=Feedback.from(writer, feedbackCreate, clockHolder);
        return feedbackRepository.save(feedback);
    }
}
