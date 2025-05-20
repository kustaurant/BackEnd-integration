package com.kustaurant.kustaurant.common.comment.domain;

import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class PostComment {

    private final Integer commentId;
    private final String commentBody;
    private ContentStatus status;
    private Integer netLikes;
    @Setter
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime updatedAt;

    private final Integer userId;
    private final Integer postId;

    private Integer likeCount;
    private Integer dislikeCount;
    private PostComment parentComment;
    private final List<PostComment> replies;

    public static PostComment create(String commentBody, Integer userId, Integer postId) {
        return PostComment.builder()
                .commentBody(commentBody)
                .status(ContentStatus.ACTIVE)
                .netLikes(0)
                .likeCount(0)
                .dislikeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userId(userId)
                .postId(postId)
                .replies(new ArrayList<>())
                .build();
    }

    public void setParent(PostComment parent) {
        this.parentComment = parent;
        parent.replies.add(this);
    }

    public void delete() {
        this.status = ContentStatus.DELETED;
        for (PostComment reply : replies) {
            reply.status = ContentStatus.DELETED;
        }
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

    public ReactionStatus toggleLike(boolean isLikedBefore, boolean isDislikedBefore) {
        if (isLikedBefore) {
            decreaseLikeCount(1);
            return ReactionStatus.LIKE_DELETED;
        }

        if (isDislikedBefore) {
            decreaseDislikeCount(1);
            increaseLikeCount(1);
            return ReactionStatus.DISLIKE_TO_LIKE;
        }

        increaseLikeCount(1);
        return ReactionStatus.LIKE_CREATED;
    }

    public ReactionStatus toggleDislike(boolean isLikedBefore, boolean isDislikedBefore) {
        if (isLikedBefore) {
            decreaseLikeCount(1);
            increaseDislikeCount(1);
            return ReactionStatus.LIKE_TO_DISLIKE;
        }

        if (isDislikedBefore) {
            decreaseDislikeCount(1);
            return ReactionStatus.DISLIKE_DELETED;
        }

        increaseDislikeCount(1);
        return ReactionStatus.DISLIKE_CREATED;
    }

    public void increaseLikeCount(int amount) {
        this.likeCount += amount;
        updateNetLikes();
    }

    public void decreaseLikeCount(int amount) {
        this.likeCount -= amount;
        updateNetLikes();
    }

    public void increaseDislikeCount(int amount) {
        this.dislikeCount += amount;
        updateNetLikes();
    }

    public void decreaseDislikeCount(int amount) {
        this.dislikeCount -= amount;
        updateNetLikes();
    }

    private void updateNetLikes() {
        this.netLikes = this.likeCount - this.dislikeCount;
    }
}
