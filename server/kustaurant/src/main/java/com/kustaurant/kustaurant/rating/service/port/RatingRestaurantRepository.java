package com.kustaurant.kustaurant.rating.service.port;

import java.util.List;

public interface RatingRestaurantRepository {

    List<Long> getRestaurantIds();
}
