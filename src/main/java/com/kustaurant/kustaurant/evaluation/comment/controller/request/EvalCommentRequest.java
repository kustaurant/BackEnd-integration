package com.kustaurant.kustaurant.evaluation.comment.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EvalCommentRequest(
        @Schema(description = "댓글 본문", example = "저두 이 의견에 동의합니당")
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
        @Size(min = 10, max = 1000)
        String body
) {
}
