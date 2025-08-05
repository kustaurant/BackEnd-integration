package com.kustaurant.kustaurant.post.post.domain.enums.depricated;

public enum DislikeStatus {
    DISLIKED, NOT_DISLIKED;

    public boolean isDisliked() {
        return this == DISLIKED;
    }
}

