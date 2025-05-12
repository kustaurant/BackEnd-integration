package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.infrastructure.PostPhotoEntity;
import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class Post {
    private Integer id;
    private String title;
    private String body;
    private String category;
    private PostStatus status;
    private Integer authorId;
    private Integer netLikeCount;
    private Integer visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PostLike> likes;
    private List<PostDislike> dislikes;
    private List<PostComment> comments;
    private List<PostPhoto> photos;
    private List<PostScrap> scraps;


    public Post(String title, String body, String category, PostStatus status, LocalDateTime createdAt, Integer authorId, Integer netLikeCount, Integer visitCount) {
        this.title = title;
        this.body = body;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.authorId = authorId;
        this.netLikeCount = netLikeCount != null ? netLikeCount : 0;
        this.visitCount = visitCount != null ? visitCount : 0;
        this.likes = new ArrayList<>();
        this.dislikes = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.scraps = new ArrayList<>();
    }

    public void increaseVisitCount() {
        this.visitCount++;
    }

    public void update(String title, String content, String category) {
        this.title = title;
        this.body = content;
        this.category = category;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }

    public void addLike(PostLike like) {
        this.likes.add(like);
    }

    public void removeLike(PostLike like) {
        this.likes.remove(like);
    }

    public void addDislike(PostDislike dislike) {
        this.dislikes.add(dislike);
    }

    public void removeDislike(PostDislike dislike) {
        this.dislikes.remove(dislike);
    }

    public void addComment(PostComment comment) {
        this.comments.add(comment);
    }

    public void addPhoto(PostPhoto photo) {
        this.photos.add(photo);
    }

    public void addScrap(PostScrap scrap) {
        this.scraps.add(scrap);
    }

    public void removeScrap(PostScrap scrap) {
        this.scraps.remove(scrap);
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

    public ReactionStatus toggleLike(User user, LocalDateTime now) {
        Optional<PostLike> like = likes.stream().filter(l -> l.getUser().equals(user)).findFirst();
        Optional<PostDislike> dislike = dislikes.stream().filter(d -> d.getUser().equals(user)).findFirst();

        if (like.isPresent()) {
            removeLike(like.get());
            decreaseLikeCount(1);
            return ReactionStatus.LIKE_DELETED;
        }

        if (dislike.isPresent()) {
            removeDislike(dislike.get());
            addLike(new PostLike(user, this, now));
            increaseLikeCount(2);
            return ReactionStatus.DISLIKE_TO_LIKE;
        }

        addLike(new PostLike(user, this, now));
        increaseLikeCount(1);
        return ReactionStatus.LIKE_CREATED;
    }

    public ReactionStatus toggleDislike(User user, LocalDateTime now) {
        Optional<PostLike> like = likes.stream().filter(l -> l.getUser().equals(user)).findFirst();
        Optional<PostDislike> dislike = dislikes.stream().filter(d -> d.getUser().equals(user)).findFirst();

        if (dislike.isPresent()) {
            removeDislike(dislike.get());
            increaseLikeCount(1);
            return ReactionStatus.DISLIKE_DELETED;
        }

        if (like.isPresent()) {
            removeLike(like.get());
            addDislike(new PostDislike(user, this, now));
            decreaseLikeCount(2);
            return ReactionStatus.LIKE_TO_DISLIKE;
        }

        addDislike(new PostDislike(user, this, now));
        decreaseLikeCount(1);
        return ReactionStatus.DISLIKE_CREATED;
    }

    public void increaseLikeCount(int amount) {
        this.netLikeCount += amount;
    }

    public void decreaseLikeCount(int amount) {
        this.netLikeCount -= amount;
    }

}

