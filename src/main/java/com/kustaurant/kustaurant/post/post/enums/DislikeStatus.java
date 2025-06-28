package com.kustaurant.kustaurant.post.post.enums;

public enum DislikeStatus {
    DISLIKED, NOT_DISLIKED;

    public boolean isDisliked() {
        return this == DISLIKED;
    }
}

