package com.kustaurant.kustaurant.common.post.enums;

public enum DislikeStatus {
    DISLIKED, NOT_DISLIKED;

    public boolean isDisliked() {
        return this == DISLIKED;
    }
}

