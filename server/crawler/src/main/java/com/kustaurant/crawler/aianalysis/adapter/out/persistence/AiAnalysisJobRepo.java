package com.kustaurant.crawler.aianalysis.adapter.out.persistence;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;

public interface AiAnalysisJobRepo {

    long save(AiAnalysisJob job);

    AiAnalysisJob findPendingJob();

    AiAnalysisJob findJob(long id);
}
