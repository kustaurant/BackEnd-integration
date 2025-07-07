package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class Post {
    private Integer id;
    private String title;
    private String body;
    private String category;
    private ContentStatus status;
    private Long authorId;
    private Integer visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Integer> commentIds;
    private List<Integer> photoIds;
    private List<Integer> scrapIds;

    public void delete() {
        this.status = ContentStatus.DELETED;
    }

    public void update(String title, String body, String category, List<String> imageUrls) {
        this.title = title;
        this.body = body;
        this.category = category;
    }

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long diffInMinutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (diffInMinutes < 1) {
            return "방금 전";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + "분 전";
        } else if (diffInMinutes < 1440) { // 24시간
            return (diffInMinutes / 60) + "시간 전";
        } else {
            return (diffInMinutes / 1440) + "일 전";
        }
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

    public void updateCommentIds(List<Integer> commentIds) {
        this.commentIds = commentIds;
    }

    public void updatePhotoIds(List<Integer> photoIds) {
        this.photoIds = photoIds;
    }

    public void updateScrapIds(List<Integer> scrapIds) {
        this.scrapIds = scrapIds;
    }
}

