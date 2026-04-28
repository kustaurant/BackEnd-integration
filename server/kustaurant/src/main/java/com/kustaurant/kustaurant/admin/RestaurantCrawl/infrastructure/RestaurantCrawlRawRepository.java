package com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure;

import com.kustaurant.map.ZoneType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCrawlRawRepository extends JpaRepository<RestaurantCrawlRawEntity, Long> {

    Optional<RestaurantCrawlRawEntity> findBySourcePlaceId(String sourcePlaceId);

    Optional<RestaurantCrawlRawEntity> findBySourceUrl(String sourceUrl);

    List<RestaurantCrawlRawEntity> findAllByCrawlStatus(String crawlStatus);

    List<RestaurantCrawlRawEntity> findAllByCrawlStatusAndCrawlScope(String crawlStatus, ZoneType crawlScope);

    List<RestaurantCrawlRawEntity> findAllBySourcePlaceIdIn(Set<String> sourcePlaceIds);
}
