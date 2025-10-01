package com.kustaurant.crawler.aianalysis.service.port;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;

public interface RatingCrawlerRepo {

    void upsertRating(long restaurantId, RestaurantAnalysis analysis);
}
