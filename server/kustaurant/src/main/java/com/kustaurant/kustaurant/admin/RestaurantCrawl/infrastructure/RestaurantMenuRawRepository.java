package com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantMenuRawRepository extends JpaRepository<RestaurantMenuCrawlRawEntity, Long> {

    void deleteByRestaurantRawId(Long restaurantRawId);

    List<RestaurantMenuCrawlRawEntity> findAllByRestaurantRawIdIn(Collection<Long> restaurantRawIds);
}
