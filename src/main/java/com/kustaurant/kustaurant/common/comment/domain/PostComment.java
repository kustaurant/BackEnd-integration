package com.kustaurant.kustaurant.common.comment.domain;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentDislike;
import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentLike;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostComment {

    private final Integer commentId;
    private final String commentBody;
    private PostStatus status;
    private Integer netLikes;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final Integer userId;
    private final Integer postId;

    private List<PostCommentLike> likes;
    private List<PostCommentDislike> dislikes;
    private PostComment parentComment;
    private final List<PostComment> replies;

    @Builder
    public PostComment(Integer commentId, String commentBody, PostStatus status, Integer netLikes,
                       LocalDateTime createdAt, LocalDateTime updatedAt,
                       Integer userId, Integer postId,
                       PostComment parentComment, List<PostComment> replies) {
        this.commentId = commentId;
        this.commentBody = commentBody;
        this.status = status;
        this.netLikes = netLikes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.postId = postId;
        this.parentComment = parentComment;
        this.replies = replies != null ? replies : new ArrayList<>();
    }

    public static PostComment create(String commentBody, Integer userId, Integer postId) {
        return PostComment.builder()
                .commentBody(commentBody)
                .status(PostStatus.ACTIVE)
                .netLikes(0)
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
        this.status = PostStatus.DELETED;
        for (PostComment reply : replies) {
            reply.status = PostStatus.DELETED;
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

    public ReactionStatus toggleLike(User user) {
        boolean liked = likes.stream().anyMatch(l -> l.isBy(user));
        boolean disliked = dislikes.stream().anyMatch(d -> d.isBy(user));

        if (liked) {
            likes.removeIf(l -> l.isBy(user));
            netLikes--;
            return ReactionStatus.LIKE_DELETED;
        } else {
            if (disliked) {
                dislikes.removeIf(d -> d.isBy(user));
                netLikes += 2;
                return ReactionStatus.DISLIKE_TO_LIKE;
            } else {
                likes.add(PostCommentLike.create(user, this));
                netLikes++;
                return ReactionStatus.LIKE_CREATED;
            }
        }
    }

    public ReactionStatus toggleDislike(User user) {
        boolean liked = likes.stream().anyMatch(l -> l.isBy(user));
        boolean disliked = dislikes.stream().anyMatch(d -> d.isBy(user));

        if (disliked) {
            dislikes.removeIf(d -> d.isBy(user));
            netLikes++;
            return ReactionStatus.DISLIKE_DELETED;
        } else {
            if (liked) {
                likes.removeIf(l -> l.isBy(user));
                netLikes -= 2;
                return ReactionStatus.LIKE_TO_DISLIKE;
            } else {
                dislikes.add(PostCommentDislike.create(user, this));
                netLikes--;
                return ReactionStatus.DISLIKE_CREATED;
            }
        }
    }
}
