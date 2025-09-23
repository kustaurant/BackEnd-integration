package com.kustaurant.mainapp.restaurant.query.search;

import java.util.List;

public interface RestaurantSearchRepository {

    List<Long> searchRestaurantIds(String[] kwArr, int size);
}
