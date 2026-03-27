package com.kustaurant.kustaurant.admin.crawl.controller.command;

public record IgRawSaveResult(
        int crawledPages,
        int rawSavedCount
) {
}
