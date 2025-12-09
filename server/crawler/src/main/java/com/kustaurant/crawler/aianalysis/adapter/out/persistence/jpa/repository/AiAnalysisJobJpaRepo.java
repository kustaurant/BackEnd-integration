package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity.AiAnalysisJobEntity;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiAnalysisJobJpaRepo extends JpaRepository<AiAnalysisJobEntity, Long> {

    AiAnalysisJobEntity findByStatusOrderByCreatedAtAsc(JobStatus status);

    @Modifying
    @Query("""
        UPDATE AiAnalysisJobEntity j
        SET j.processedReviews = j.processedReviews + 1,
          j.updatedAt = :now
        WHERE j.id = :jobId
    """)
    void increaseProcessed(@Param("jobId") long jobId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
        UPDATE AiAnalysisJobEntity j
        SET j.failedReviews = j.failedReviews + 1,
          j.updatedAt = :now
        WHERE j.id = :jobId
    """)
    void increaseFailed(@Param("jobId") long jobId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("""
        UPDATE AiAnalysisJobEntity j
        SET j.status = :status
        WHERE j.id = :jobId
        AND j.status != :status
        AND j.totalReviews = j.processedReviews + j.failedReviews
    """)
    int changeStatus(@Param("jobId") long jobId, @Param("status") JobStatus status);
}
