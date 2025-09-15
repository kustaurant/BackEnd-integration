package com.kustaurant.kustaurant.common.enums;

public enum ReactionType {
    LIKE, DISLIKE, NONE;

    public boolean isLike() {
        return this == LIKE;
    }
}
