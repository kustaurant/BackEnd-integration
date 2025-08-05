package com.kustaurant.kustaurant.post.post.domain.enums.depricated;

public enum LikeStatus {
    LIKED, NOT_LIKED;

    public boolean isLiked() {
        return this == LIKED;
    }
}

