package com.kustaurant.kustaurant.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "예외 상황시")
public class ErrorResponse {
    @Schema(description = "예외 상황", example = "BAD REQUEST")
    private String status;
    @Schema(description = "구체적 예외 메시지", example = "이러이러해서 오류가 발생했습니다.")
    private String message;
}
