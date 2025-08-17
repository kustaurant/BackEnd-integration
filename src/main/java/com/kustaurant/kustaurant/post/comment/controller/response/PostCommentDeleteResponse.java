package com.kustaurant.kustaurant.post.comment.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostCommentDeleteResponse(
        Integer id,
        @Schema(description = "대댓글이였다면 부모댓글id, 그냥 댓글일경우 미반환(null)")
        Integer parentCommentId,
        @Schema(description = "삭제 요청한 댓글이 DELETED 인지 PENDING 인지")
        PostCommentStatus status,
        @Schema(description = "실제로 제거되는 댓글 id목록")
        List<Integer> removeIds,
        @Schema(description = "게시글에 표시될 총 댓글수 (DELETED만 제외, 나머지 포함) ")
        Long postTotalCommentCount
) {
}
