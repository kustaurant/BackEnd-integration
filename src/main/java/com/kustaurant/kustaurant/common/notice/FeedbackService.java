package com.kustaurant.kustaurant.common.notice;

import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.Feedback;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public String addFeedback(String feedbackBody, Principal principal) {
        Optional<User> userOptional = userRepository.findByProviderId(principal.getName());

        if (userOptional.isEmpty()) {
            return "fail";
        }

        Feedback newFeedback = new Feedback();
        newFeedback.setFeedbackContent(feedbackBody);
        newFeedback.setUser(userOptional.get());
        newFeedback.setCreatedAt(LocalDateTime.now());
        newFeedback.setStatus("ACTIVE");

        feedbackRepository.save(newFeedback);
        return "success";
    }

    public void addApiFeedback(String feedbackBody, Integer UserId) {
        Optional<User> userOptional = userRepository.findByUserId(UserId);
        if (userOptional.isEmpty()) {
            return;
        }

        Feedback newFeedback = new Feedback();
        newFeedback.setFeedbackContent(feedbackBody);
        newFeedback.setUser(userOptional.get());
        newFeedback.setCreatedAt(LocalDateTime.now());
        newFeedback.setStatus("ACTIVE");

        feedbackRepository.save(newFeedback);
    }

}
