package com.kustaurant.kustaurant.admin.report;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    // 1. 신고하기
    @Operation(summary = "리뷰 댓글 신고하기", description = "신고하기를 누르면 \"정말 신고하시겠습니까?\" 다이얼로그를 띄우고, 이 api를 호출하시면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고에 성공했습니다.", content = {@Content(schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.\n\n또한 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @PostMapping("/api/v1/auth/restaurants/{restaurantId}/comments/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {



        return new ResponseEntity<>(HttpStatus.OK);
    }
}
