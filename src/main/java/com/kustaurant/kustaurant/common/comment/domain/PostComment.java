package com.kustaurant.kustaurant.common.comment.domain;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
public class PostComment {
    private final Integer commentId;
    private final String commentBody;
    private final String status;
    private final Integer likeCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final Integer userId;
    private final Integer postId;
    private final Integer parentCommentId;
    private final List<PostComment> replies;

    @Builder
    public PostComment(Integer commentId, String commentBody, String status, Integer likeCount,
                       LocalDateTime createdAt, LocalDateTime updatedAt,
                       Integer userId, Integer postId, Integer parentCommentId,
                       List<PostComment> replies) {
        this.commentId = commentId;
        this.commentBody = commentBody;
        this.status = status;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
        this.replies = replies;
    }

    public static PostComment from(PostCommentEntity entity) {
        return new PostComment(
                entity.getCommentId(),
                entity.getCommentBody(),
                entity.getStatus(),
                entity.getLikeCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getUser().getUserId(),
                entity.getPost().getPostId(),
                entity.getParentComment() != null ? entity.getParentComment().getCommentId() : null,
                entity.getRepliesList().stream()
                        .map(PostComment::from)
                        .toList()
        );
    }

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) return "";

        long years = ChronoUnit.YEARS.between(createdAt, now);
        if (years > 0) return years + "년 전";

        long months = ChronoUnit.MONTHS.between(createdAt, now);
        if (months > 0) return months + "달 전";

        long days = ChronoUnit.DAYS.between(createdAt, now);
        if (days > 0) return days + "일 전";

        long hours = ChronoUnit.HOURS.between(createdAt, now);
        if (hours > 0) return hours + "시간 전";

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes > 0) return minutes + "분 전";

        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        return seconds + "초 전";
    }


}
