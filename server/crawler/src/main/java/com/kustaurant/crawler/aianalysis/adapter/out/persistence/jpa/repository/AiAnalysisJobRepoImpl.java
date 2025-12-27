package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity.AiAnalysisJobEntity;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisJobRepo;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AiAnalysisJobRepoImpl implements AiAnalysisJobRepo {

    private final AiAnalysisJobJpaRepo jpaRepo;

    @Override
    public long save(AiAnalysisJob job) {
        return jpaRepo.save(AiAnalysisJobEntity.from(job)).getId();
    }

    @Override
    public AiAnalysisJob findPendingJob() {
        return jpaRepo.findByStatusOrderByCreatedAtAsc(JobStatus.PENDING).toModel();
    }

    @Override
    public AiAnalysisJob findJob(long id) {
        return jpaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("no job id: " + id))
                .toModel();
    }

    @Override
    public void updateReviewCount(long id, boolean success, LocalDateTime now) {
        if (success) {
            jpaRepo.increaseProcessed(id, now);
        } else {
            jpaRepo.increaseFailed(id, now);
        }
    }

    @Override
    public int changeStatus(long id, JobStatus status) {
        return jpaRepo.changeStatus(id, status);
    }
}
