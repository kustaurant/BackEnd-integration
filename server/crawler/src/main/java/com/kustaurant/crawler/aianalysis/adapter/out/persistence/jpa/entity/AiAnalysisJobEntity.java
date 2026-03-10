package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.entity;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_analysis_job")
public class AiAnalysisJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING','RUNNING','DONE','FAILED')")
    private JobStatus status;

    @Column(name = "total_reviews", nullable = false)
    private int totalReviews;

    @Column(name = "processed_reviews", nullable = false)
    private int processedReviews;

    @Column(name = "failed_reviews", nullable = false)
    private int failedReviews;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public static AiAnalysisJobEntity from(AiAnalysisJob domain) {
        return AiAnalysisJobEntity.builder()
                .id(domain.getId())
                .version(domain.getVersion())
                .restaurantId(domain.getRestaurantId())
                .status(domain.getStatus())
                .totalReviews(domain.getTotalReviews())
                .processedReviews(domain.getProcessedReviews())
                .failedReviews(domain.getFailedReviews())
                .startedAt(domain.getStartedAt())
                .completedAt(domain.getCompletedAt())
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public AiAnalysisJob toModel() {
        return AiAnalysisJob.builder()
                .id(this.id)
                .version(this.version)
                .restaurantId(this.restaurantId)
                .status(this.status)
                .totalReviews(this.totalReviews)
                .processedReviews(this.processedReviews)
                .failedReviews(this.failedReviews)
                .startedAt(this.startedAt)
                .completedAt(this.completedAt)
                .errorMessage(this.errorMessage)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
