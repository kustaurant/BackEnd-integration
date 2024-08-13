package com.kustaurant.restauranttier.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "404")
    private String status;
    @Schema(description = "메시지", example = "해당 조건에 맞는 맛집이 존재하지 않습니다.")
    private String message;
}
