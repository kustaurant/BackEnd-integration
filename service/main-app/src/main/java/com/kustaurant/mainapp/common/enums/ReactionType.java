package com.kustaurant.mainapp.common.enums;

public enum ReactionType {
    LIKE, DISLIKE;

    public boolean isLike() {
        return this == LIKE;
    }
}
