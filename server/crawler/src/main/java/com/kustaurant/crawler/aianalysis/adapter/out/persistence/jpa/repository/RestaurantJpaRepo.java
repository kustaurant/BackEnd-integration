package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.jpa.restaurant.entity.RestaurantEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantJpaRepo extends JpaRepository<RestaurantEntity, Long> {

    @Query("""
    SELECT r
    FROM RestaurantEntity r
    LEFT JOIN AiAnalysisJobEntity j
      ON j.restaurantId = r.restaurantId
     AND j.id = (
           SELECT MAX(j2.id)
           FROM AiAnalysisJobEntity j2
           WHERE j2.restaurantId = r.restaurantId
         )
    WHERE r.status = :restaurantStatus
      AND (j.completedAt IS NULL OR j.completedAt < :before)
      AND (j.status IS NULL OR j.status IN :endStatus)
    ORDER BY r.restaurantId
    """)
    List<RestaurantEntity> findRestaurantsForCrawling(
            @Param("restaurantStatus") String restaurantStatus,
            @Param("before") LocalDateTime before,
            @Param("endStatus") List<String> endStatus
    );
}
