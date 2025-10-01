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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "user-report-controller",
        description = "통합 신고기능 - 미구현, 추후 구현가능성 있음"
)
public class ReportApiController {


    // 1. 신고하기
    @Operation(summary = "통합 신고(평가, 평가-댓글, 게시글, 게시글-댓글, 게시글-대댓글)", description = "신고하기를 누르면 \"정말 신고하시겠습니까?\" 다이얼로그를 띄우고, 이 api를 호출하시면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고에 성공했습니다.", content = {@Content(schema = @Schema(implementation = Void.class))}),
    })
    @PostMapping("/api/v2/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Long restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // TODO: 추후 구현



        return new ResponseEntity<>(HttpStatus.OK);
    }
}
