package com.kustaurant.crawler.aianalysis.adapter.out.persistence;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import java.time.LocalDateTime;

public interface AiAnalysisJobRepo {

    long save(AiAnalysisJob job);

    AiAnalysisJob findPendingJob();

    AiAnalysisJob findJob(long id);

    void updateReviewCount(long id, boolean success, LocalDateTime now);

    int changeStatus(long id, JobStatus status);
}
