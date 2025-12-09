package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity.AiAnalysisReviewEntity;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAnalysisReviewJpaRepo extends JpaRepository<AiAnalysisReviewEntity, Long> {

    List<AiAnalysisReview> findByJobId(long jobId);
}
