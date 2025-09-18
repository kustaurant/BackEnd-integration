package com.kustaurant.kustaurant.restaurant.query.home;

import java.util.List;

public interface RestaurantHomeRepository {

    List<Long> getTopRestaurantIds(int size);

    List<Long> getRandomRestaurantIds(int size);
}
