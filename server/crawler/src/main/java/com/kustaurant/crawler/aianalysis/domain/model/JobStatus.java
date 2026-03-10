package com.kustaurant.crawler.aianalysis.domain.model;

import java.util.List;

public enum JobStatus {
    PENDING,
    RUNNING,
    DONE,
    FAILED;

    public static List<String> getEndStatus() {
        return List.of("DONE", "FAILED");
    }
}
