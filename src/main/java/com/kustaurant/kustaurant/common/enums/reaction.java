package com.kustaurant.kustaurant.common.enums;

public enum reaction {
    LIKE, DISLIKE;

    public boolean isLike() {
        return this == LIKE;
    }
}
