package com.kustaurant.crawler.aianalysis.service.port;

import java.util.List;

public interface RestaurantCrawlerRepo {

    List<RestaurantCrawlingInfo> getRestaurantsForCrawling();
}
