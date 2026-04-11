package com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantMenuRawRepository extends JpaRepository<RestaurantMenuCrawlRawEntity, Long> {

    void deleteByRestaurantRawId(Long restaurantRawId);
}
