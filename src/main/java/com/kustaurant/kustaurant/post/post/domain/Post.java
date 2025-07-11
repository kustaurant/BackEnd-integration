package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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


    /** 한국어 기준 “3시간 전” / “15초 전” 반환 */
    public String calculateTimeAgo() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.createdAt;

        long years   = ChronoUnit.YEARS  .between(past, now);
        if (years   > 0) return years   + "년 전";

        long months  = ChronoUnit.MONTHS .between(past, now);
        if (months  > 0) return months  + "달 전";

        long days    = ChronoUnit.DAYS   .between(past, now);
        if (days    > 0) return days    + "일 전";

        long hours   = ChronoUnit.HOURS  .between(past, now);
        if (hours   > 0) return hours   + "시간 전";

        long minutes = ChronoUnit.MINUTES.between(past, now);
        if (minutes > 0) return minutes + "분 전";

        long seconds = ChronoUnit.SECONDS.between(past, now);
        return seconds + "초 전";
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

