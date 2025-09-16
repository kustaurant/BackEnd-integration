package com.kustaurant.kustaurant.admin.feedback.controller;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.admin.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.admin.feedback.service.FeedbackServiceImpl;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedbackApiController {
    private final FeedbackService feedbackService;

    //피드백 보내기
    @Operation(
            summary = "피드백 보내기",
            description = "사용자 피드백을 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 감사합니다!"),
            @ApiResponse(responseCode = "400", description = "내용이 없습니다."),
    })
    @PostMapping("/v2/auth/mypage/feedback")
    public ResponseEntity<String> sendFeedback(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Valid @RequestBody FeedbackRequest req
    ){
        feedbackService.create(user.id(),req);

        return ResponseEntity.ok("피드백 감사합니다!");
    }
}
