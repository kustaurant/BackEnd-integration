package com.kustaurant.crawler.aianalysis.service.port;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;

public interface AnalyzeReviewService {

    void analyzeAndSave(AiAnalysisReq req);
}
