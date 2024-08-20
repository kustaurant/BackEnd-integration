package com.kustaurant.restauranttier.tab5_mypage.service;

import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.Feedback;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.FeedbackRepository;
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

    public String addApiFeedback(String feedbackBody, Integer UserId) {
        Optional<User> userOptional = userRepository.findByUserId(UserId);
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

}
