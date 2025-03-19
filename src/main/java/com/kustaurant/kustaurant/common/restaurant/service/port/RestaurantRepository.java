package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Optional<RestaurantDomain> findById(Integer id);
    Optional<RestaurantDomain> findByIdAndStatus(Integer id, String status);
    List<RestaurantDomain> findByStatus(String status);
    List<RestaurantDomain> findByCuisineAndStatus(String cuisine, String status);
    List<RestaurantDomain> findByPositionAndStatus(String position, String status);
    List<RestaurantDomain> findByCuisineAndPositionAndStatus(String cuisine, String position, String status);

    RestaurantDomain save(RestaurantDomain restaurantDomain);
}
