package com.kustaurant.kustaurant.post.comment.domain;

import com.kustaurant.kustaurant.post.post.domain.enums.depricated.ReactionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class PostComment {

    private Integer id;
    private final String commentBody;
    private PostCommentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final Long userId;
    private Integer postId;

    private Integer parentCommentId;
    private List<Integer> replyIds;

    public static PostComment create(String commentBody, Long userId, Integer postId) {
        return PostComment.builder()
                .commentBody(commentBody)
                .status(PostCommentStatus.ACTIVE)
                .userId(userId)
                .postId(postId)
                .replyIds(new ArrayList<>())
                .build();
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public void delete() {
        this.status = PostCommentStatus.DELETED;
        // 대댓글들은 별도 서비스에서 처리
    }


    public ReactionStatus toggleLike(boolean isLikedBefore, boolean isDislikedBefore) {
        if (isLikedBefore) {
            return ReactionStatus.LIKE_DELETED;
        }

        if (isDislikedBefore) {
            return ReactionStatus.DISLIKE_TO_LIKE;
        }

        return ReactionStatus.LIKE_CREATED;
    }

    public ReactionStatus toggleDislike(boolean isLikedBefore, boolean isDislikedBefore) {
        if (isLikedBefore) {
            return ReactionStatus.LIKE_TO_DISLIKE;
        }

        if (isDislikedBefore) {
            return ReactionStatus.DISLIKE_DELETED;
        }

        return ReactionStatus.DISLIKE_CREATED;
    }
}
