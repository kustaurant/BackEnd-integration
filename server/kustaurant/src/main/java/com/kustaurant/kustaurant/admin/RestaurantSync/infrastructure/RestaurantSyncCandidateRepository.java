package com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure;

import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantSyncCandidateRepository extends JpaRepository<RestaurantSyncCandidateEntity, Long> {

    boolean existsByPlaceIdAndCandidateTypeAndCandidateStatus(
            String placeId,
            SyncCandidateType candidateType,
            SyncCandidateStatus candidateStatus
    );

    Optional<RestaurantSyncCandidateEntity> findByIdAndCandidateStatus(Long id, SyncCandidateStatus status);

    List<RestaurantSyncCandidateEntity> findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus status);
}
