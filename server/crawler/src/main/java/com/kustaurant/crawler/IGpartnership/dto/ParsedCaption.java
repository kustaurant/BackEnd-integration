package com.kustaurant.crawler.IGpartnership;

public record ParsedCaption(
        String partner,
        String benefit,
        String location,
        String contact
) {
    public boolean hasRequiredFields() {
        return hasText(partner) && hasText(benefit);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
