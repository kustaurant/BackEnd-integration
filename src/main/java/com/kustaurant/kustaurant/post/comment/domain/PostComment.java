package com.kustaurant.kustaurant.post.comment.domain;

import com.kustaurant.kustaurant.global.exception.exception.auth.AccessDeniedException;
import com.kustaurant.kustaurant.post.comment.controller.request.PostCommentRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
public class PostComment {

    private Integer id;
    private String body;
    private PostCommentStatus status;

    private final Long writerId;
    private Long postId;

    private Integer parentCommentId;
    private Set<Integer> replyIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostComment create(Long postId, PostCommentRequest req,Long userId) {
        return PostComment.builder()
                .body(req.content())
                .status(PostCommentStatus.ACTIVE)
                .writerId(userId)
                .postId(postId)
                .parentCommentId(req.parentCommentId()==null?null:req.parentCommentId())
                .build();
    }


    public void ensureWriterBy(Long userId) {
        if (!this.writerId.equals(userId))
            throw new AccessDeniedException();
    }

    public void pendingDelete() {
        this.status = PostCommentStatus.PENDING;
        this.body = "삭제된 댓글입니다.";
    }
    
    public boolean hasActiveReplies(long activeReplyCount) {
        return activeReplyCount > 0;
    }

    public boolean isReplyComment() {
        return this.parentCommentId != null;
    }

}
