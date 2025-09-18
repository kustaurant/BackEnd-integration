package com.kustaurant.kustaurant.restaurant.query.search;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import java.util.List;

public interface RestaurantSearchRepository {

    List<Long> searchRestaurantIds(String[] kwArr, int size);
}
