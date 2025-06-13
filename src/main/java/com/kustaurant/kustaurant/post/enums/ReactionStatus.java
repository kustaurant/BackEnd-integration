package com.kustaurant.kustaurant.post.enums;

public enum ReactionStatus {
    LIKE_CREATED(1),
    LIKE_DELETED(0),
    DISLIKE_TO_LIKE(1),
    DISLIKE_CREATED(-1),
    DISLIKE_DELETED(0),
    LIKE_TO_DISLIKE(-1);

    private final int appLikeStatus;

    ReactionStatus(int appLikeStatus) {
        this.appLikeStatus = appLikeStatus;
    }

    public int toAppLikeStatus() {
        return appLikeStatus;
    }
}


