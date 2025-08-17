package com.kustaurant.kustaurant.post.comment.controller.response;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public record PostCommReactionResponse(
        @Schema(description = "댓글 좋아요 수", example = "10")
        int likeCount,

        @Schema(description = "댓글 싫어요 수", example = "3")
        int dislikeCount,

        @Schema(description = "현재 사용자의 리액션(없으면 null)")
        ReactionType reactionType
) { }
