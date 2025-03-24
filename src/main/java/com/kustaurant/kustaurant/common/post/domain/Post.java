package com.kustaurant.kustaurant.common.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class Post {
    private final Integer postId;
    private final String postTitle;
    private final String postBody;
    private final String postCategory;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Integer postVisitCount;
    private final Integer likeCount;
    private final Integer userId;

    @Builder
    public Post(Integer postId, String postTitle, String postBody, String postCategory, String status,
                LocalDateTime createdAt, LocalDateTime updatedAt, Integer postVisitCount, Integer likeCount, Integer userId) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postCategory = postCategory;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postVisitCount = postVisitCount;
        this.likeCount = likeCount;
        this.userId = userId;
    }

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.createdAt;

        long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference + "년 전";

        long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference + "달 전";

        long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference + "일 전";

        long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference + "시간 전";

        long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference + "분 전";

        long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference + "초 전";
    }
}

