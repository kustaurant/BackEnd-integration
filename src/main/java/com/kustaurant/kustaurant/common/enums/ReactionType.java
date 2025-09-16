package com.kustaurant.kustaurant.common.enums;

public enum ReactionType {
    LIKE, DISLIKE;

    public boolean isLike() {
        return this == LIKE;
    }
}
