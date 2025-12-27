package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.repository;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.RestaurantCrawlingInfo;
import com.kustaurant.crawler.aianalysis.domain.model.JobStatus;
import com.kustaurant.jpa.restaurant.entity.RestaurantEntity;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantCrawlerRepoImpl implements RestaurantCrawlerRepo {

    private final RestaurantJpaRepo restaurantJpaRepo;
    private final Clock clock;

    @Override
    public List<RestaurantCrawlingInfo> getRestaurantsForCrawling() {
        List<RestaurantEntity> restaurants = restaurantJpaRepo.findRestaurantsForCrawling(
                "ACTIVE",
                LocalDateTime.now(clock).minusMonths(1),
                JobStatus.getEndStatus()
        );

        return restaurants
                .stream()
                .map(r -> new RestaurantCrawlingInfo(r.getRestaurantId(), r.getRestaurantUrl()))
                .toList();
    }

    @Override
    public String getRestaurantUrl(long id) {
        return restaurantJpaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("no restaurant id: " + id))
                .getRestaurantUrl();
    }
}
