package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import java.util.List;
import java.util.Map;

public interface RatingRestaurantRepository {

    List<Integer> getRestaurantIds();
    Map<Integer, RestaurantStats> getRestaurantStatsByIds(List<Integer> ids);
}
