package com.kustaurant.kustaurant.post.post.enums;

public enum LikeStatus {
    LIKED, NOT_LIKED;

    public boolean isLiked() {
        return this == LIKED;
    }
}

