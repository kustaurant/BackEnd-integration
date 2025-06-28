package com.kustaurant.kustaurant.admin.feedback.controller;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.admin.feedback.service.FeedbackServiceImpl;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class FeedbackWebController {
    private final FeedbackServiceImpl feedbackServiceImpl;

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/feedback")
    public ResponseEntity<String> submitFeedback(
            @AuthUser AuthUserInfo user,
            @Valid @RequestBody FeedbackRequest req
    ) {
        feedbackServiceImpl.create(user.id(),req);

        return ResponseEntity.ok("피드백 감사합니다!");
    }

}
