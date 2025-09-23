package com.kustaurant.mainapp.rating.service.port;

import com.kustaurant.mainapp.rating.domain.model.RestaurantStats;
import java.util.List;

public interface RatingRestaurantRepository {

    List<Long> getRestaurantIds();
    List<RestaurantStats> getRestaurantStatsByIds(List<Long> ids);
}
