package com.kustaurant.crawler.aianalysis.infrastructure.repository;

import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlerRepo;
import com.kustaurant.crawler.aianalysis.service.port.RestaurantCrawlingInfo;
import com.kustaurant.crawler.global.config.TimeConfig;
import com.kustaurant.jpa.restaurant.entity.RestaurantEntity;
import com.kustaurant.jpa.restaurant.repository.RestaurantJpaRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantCrawlerRepoImpl implements RestaurantCrawlerRepo {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final Clock clock;

    @Override
    public List<RestaurantCrawlingInfo> getRestaurantsForCrawling() {
        return restaurantJpaRepository
                .findByStatusAndAiProcessedAtBefore("ACTIVE", LocalDateTime.now(clock).minusMonths(1))
                .stream()
                .map(RestaurantCrawlerRepoImpl::mapToDto)
                .toList();
    }

    private static RestaurantCrawlingInfo mapToDto(RestaurantEntity entity) {
        return new RestaurantCrawlingInfo(
                entity.getRestaurantId(), entity.getRestaurantUrl()
        );
    }
}
