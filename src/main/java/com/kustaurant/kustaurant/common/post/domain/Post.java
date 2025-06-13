package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class Post {
    private Integer id;
    private String title;
    private String body;
    private String category;
    private ContentStatus status;
    private Integer authorId;
    private Integer netLikes;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private List<PostComment> comments;
    private List<PostPhoto> photos;
    private List<PostScrap> scraps;


    public void delete() {
        this.status = ContentStatus.DELETED;
    }

    public void update(String title, String body, String category, List<String> imageUrls) {
        this.title = title;
        this.body = body;
        this.category = category;

        this.photos = new java.util.ArrayList<>();
        for (String url : imageUrls) {
            this.photos.add(PostPhoto.builder()
                    .postId(this.id)
                    .photoImgUrl(url)
                    .status(ContentStatus.ACTIVE)
                    .build());
        }
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

