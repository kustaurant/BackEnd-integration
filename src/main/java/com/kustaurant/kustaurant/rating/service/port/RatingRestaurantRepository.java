package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.RestaurantStats;
import java.util.List;

public interface RatingRestaurantRepository {

    List<Integer> getRestaurantIds();
    List<RestaurantStats> getRestaurantStatsByIds(List<Integer> ids);
}
