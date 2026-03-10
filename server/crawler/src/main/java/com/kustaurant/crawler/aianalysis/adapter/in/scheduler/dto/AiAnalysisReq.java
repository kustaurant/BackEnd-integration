package com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto;

public record AiAnalysisReq(
        long jobId,
        long restaurantId,
        String review
) {

}
