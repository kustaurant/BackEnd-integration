package com.kustaurant.kustaurant.restaurant.query.search;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import java.util.List;

public interface RestaurantSearchRepository {

    List<RestaurantCoreInfoDto> search(String[] kwArr, Long userId, int size);
}
