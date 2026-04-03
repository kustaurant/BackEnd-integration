package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.admin.crawl.dto.RestaurantMatchCandidate;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant getByIdAndStatus(Long id, String status);
    void increaseVisitCount(Long restaurantId);
}
