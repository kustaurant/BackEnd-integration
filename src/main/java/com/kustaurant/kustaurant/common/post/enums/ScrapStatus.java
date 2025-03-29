package com.kustaurant.kustaurant.common.post.enums;

public enum ScrapStatus {
    SCRAPPED, NOT_SCRAPPED;

    public boolean isScrapped() {
        return this == SCRAPPED;
    }
}

