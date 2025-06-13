package com.kustaurant.kustaurant.post.enums;

public enum ScrapStatus {
    SCRAPPED, NOT_SCRAPPED;

    public boolean isScrapped() {
        return this == SCRAPPED;
    }
}

