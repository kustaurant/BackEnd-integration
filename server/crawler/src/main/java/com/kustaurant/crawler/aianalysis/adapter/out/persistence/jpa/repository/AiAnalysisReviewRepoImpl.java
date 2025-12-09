package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisReviewRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity.AiAnalysisReviewEntity;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AiAnalysisReviewRepoImpl implements AiAnalysisReviewRepo {

    private final AiAnalysisReviewJpaRepo jpaRepo;

    @Override
    public void save(AiAnalysisReview review) {
        jpaRepo.save(AiAnalysisReviewEntity.from(review));
    }

    @Override
    public List<AiAnalysisReview> findAllByJobId(long jobId) {
        return jpaRepo.findByJobId(jobId);
    }
}
