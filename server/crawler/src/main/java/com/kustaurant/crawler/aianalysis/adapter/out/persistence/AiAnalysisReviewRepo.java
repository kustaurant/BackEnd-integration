package com.kustaurant.crawler.aianalysis.adapter.out.persistence;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import java.util.List;

public interface AiAnalysisReviewRepo {

    void save(AiAnalysisReview aiAnalysisReview);

    List<AiAnalysisReview> findAllByJobId(long jobId);
}
