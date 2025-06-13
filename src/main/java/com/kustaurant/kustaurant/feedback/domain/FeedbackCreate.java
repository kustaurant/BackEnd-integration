package com.kustaurant.kustaurant.feedback.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FeedbackCreate {
    private final String comment;

    @Builder
    public FeedbackCreate(@JsonProperty("comment") String comment) {
        this.comment = comment;
    }
}
