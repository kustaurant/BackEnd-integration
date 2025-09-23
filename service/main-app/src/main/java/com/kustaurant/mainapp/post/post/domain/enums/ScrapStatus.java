package com.kustaurant.mainapp.post.post.domain.enums;

public enum ScrapStatus {
    SCRAPPED, NOT_SCRAPPED;
    public boolean isScrapped() {
        return this == SCRAPPED;
    }
}

