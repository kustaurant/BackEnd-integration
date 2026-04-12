package com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCrawlRawRepository extends JpaRepository<RestaurantCrawlRawEntity, Long> {

    Optional<RestaurantCrawlRawEntity> findBySourcePlaceId(String sourcePlaceId);

    Optional<RestaurantCrawlRawEntity> findBySourceUrl(String sourceUrl);
}
