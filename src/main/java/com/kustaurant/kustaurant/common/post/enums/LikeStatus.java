package com.kustaurant.kustaurant.common.post.enums;

public enum LikeStatus {
    LIKED, NOT_LIKED;

    public boolean isLiked() {
        return this == LIKED;
    }
}

