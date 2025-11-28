package com.kustaurant.crawler.aianalysis.adapter.out.persistence;

import com.kustaurant.crawler.aianalysis.adapter.out.persistence.dto.RestaurantCrawlingInfo;
import java.util.List;

public interface RestaurantCrawlerRepo {

    List<RestaurantCrawlingInfo> getRestaurantsForCrawling();

    String getRestaurantUrl(long id);
}
