package com.kustaurant.mainapp.post.comment.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostCommentRequest(
        @NotBlank(message = "댓글 내용은 비어 있을 수 없습니다.")
        @Size(min = 1, max = 10000, message = "댓글 내용은 1자 이상 10,000자 이하로 입력해야 합니다.")
        String content,
        @Schema(
                description = "대댓글을 작성할 경우, 부모 댓글의 ID를 입력합니다. null이면 일반 댓글로 처리합니다.",
                example = "456"
        )
        Long parentCommentId
) {
}
