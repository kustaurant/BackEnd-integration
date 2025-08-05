package com.kustaurant.kustaurant.post.post.domain.enums;

public enum ScrapStatus {
    SCRAPPED, NOT_SCRAPPED;
    public boolean isScrapped() {
        return this == SCRAPPED;
    }
}

