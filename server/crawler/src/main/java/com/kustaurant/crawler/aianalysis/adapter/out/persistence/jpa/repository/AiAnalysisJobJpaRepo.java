package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity.AiAnalysisJobEntity;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiAnalysisJobJpaRepo extends JpaRepository<AiAnalysisJobEntity, Long> {

    AiAnalysisJobEntity findByStatusOrderByCreatedAtAsc(JobStatus status);
}
