package com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_sync_candidate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantSyncCandidateEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false, length = 64)
    private String placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "candidate_type", nullable = false, length = 16)
    private SyncCandidateType candidateType;

    @Enumerated(EnumType.STRING)
    @Column(name = "candidate_status", nullable = false, length = 16)
    private SyncCandidateStatus candidateStatus;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "reviewed_by", length = 64)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    private RestaurantSyncCandidateEntity(String placeId, SyncCandidateType candidateType, String reason) {
        this.placeId = placeId;
        this.candidateType = candidateType;
        this.candidateStatus = SyncCandidateStatus.PENDING;
        this.reason = reason;
    }

    public static RestaurantSyncCandidateEntity pending(String placeId, SyncCandidateType candidateType, String reason) {
        return new RestaurantSyncCandidateEntity(placeId, candidateType, reason);
    }

    public void approve(String reviewedBy) {
        this.candidateStatus = SyncCandidateStatus.APPROVED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(String reviewedBy) {
        this.candidateStatus = SyncCandidateStatus.REJECTED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
    }

    public void markApplied() {
        this.appliedAt = LocalDateTime.now();
    }
}
