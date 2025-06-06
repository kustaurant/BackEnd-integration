package com.kustaurant.kustaurant.common.feedback.controller;

import com.kustaurant.kustaurant.common.feedback.domain.FeedbackDTO;
import com.kustaurant.kustaurant.common.feedback.service.FeedbackServiceImpl;
import com.kustaurant.kustaurant.common.user.controller.api.response.MypageErrorDTO;
import com.kustaurant.kustaurant.global.auth.jwt.customAnno.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FeedbackApiController {
    private final FeedbackServiceImpl feedbackServiceImpl;

    //피드백 보내기
    @Operation(
            summary = "피드백 보내기 기능입니다",
            description = "피드백을 보냅니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 감사합니다."),
            @ApiResponse(responseCode = "400", description = "내용이 없습니다."),
    })
    @PostMapping("/auth/mypage/feedback")
    public ResponseEntity<MypageErrorDTO> sendFeedback(
            @Parameter(hidden = true) @JwtToken Integer userId,
            @RequestBody FeedbackDTO feedbackDTO
    ){
        String comments = feedbackDTO.getComments();
        MypageErrorDTO response = new MypageErrorDTO();

        if (comments == null) {
            response.setError("내용이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
//        feedbackServiceImpl.create()
        response.setError("피드백 감사합니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
