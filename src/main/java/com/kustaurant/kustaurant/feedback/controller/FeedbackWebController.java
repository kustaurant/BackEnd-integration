package com.kustaurant.kustaurant.feedback.controller;

import com.kustaurant.kustaurant.feedback.domain.FeedbackCreate;
import com.kustaurant.kustaurant.feedback.service.FeedbackServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class FeedbackWebController {
    private final FeedbackServiceImpl feedbackServiceImpl;

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/feedback")
    public ResponseEntity<String> submitFeedback(
            @RequestBody FeedbackCreate feedbackCreate,
            Principal principal
    ) {
        if (feedbackCreate.getComment() == null || feedbackCreate.getComment().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("내용이 없습니다.");
        }

        Integer userId = Integer.valueOf(principal.getName());
        feedbackServiceImpl.create(feedbackCreate, userId);

        return ResponseEntity.ok("피드백 감사합니다");
    }

}
