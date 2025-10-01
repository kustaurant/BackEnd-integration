package com.kustaurant.crawler.aianalysis.domain.model;

import lombok.Getter;

@Getter
public enum Sentiment {

    POSITIVE(1),
    NEGATIVE(-1),
    NEUTRAL(0);

    private final int value;

    Sentiment(int value) {
        this.value = value;
    }
}
