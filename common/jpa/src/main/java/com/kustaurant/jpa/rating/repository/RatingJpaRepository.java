package com.kustaurant.jpa.rating.repository;

import com.kustaurant.jpa.rating.entity.RatingEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingJpaRepository  extends JpaRepository<RatingEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RatingEntity r where r.restaurantId in :ids")
    List<RatingEntity> findAllByIdInForUpdate(@Param("ids") List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RatingEntity r where r.restaurantId = :id")
    Optional<RatingEntity> findByIdForUpdate(@Param("id") long id);

    @Query("select r from RatingEntity r "
            + "where r.aiProcessedAt is not null and r.aiReviewCount > 0")
    List<RatingEntity> findAiProcessed();
}
