package com.kustaurant.dataprocessor.aianalysis.domain.model;

public record Review(
        String body
) {

    private static final int MIN_REVIEW_LENGTH = 10;

    public Review(String body) {
        if (!isValid(body)) {
            throw new IllegalArgumentException("Invalid review");
        }
        this.body = refineBody(body);
    }

    public static boolean isValid(String body) {
        return refineBody(body).length() > MIN_REVIEW_LENGTH;
    }

    private static String refineBody(String body) {
        return body.replaceAll("\\s+", " ").trim();
    }
}
