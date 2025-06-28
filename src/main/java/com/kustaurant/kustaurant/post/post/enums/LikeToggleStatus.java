package com.kustaurant.kustaurant.post.post.enums;

public enum LikeToggleStatus {
    CREATED(1), DELETED(0);

    private final int value;

    LikeToggleStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

