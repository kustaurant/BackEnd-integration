package com.kustaurant.kustaurant.common.restaurant.application.service.command.port;

import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Restaurant getById(Integer id);
    Restaurant getByIdAndStatus(Integer id, String status);
    List<Restaurant> findByCuisineAndStatus(String cuisine, String status);
    List<Restaurant> findByPositionAndStatus(String position, String status);
    List<Restaurant> findByCuisineAndPositionAndStatus(String cuisine, String position, String status);

    Restaurant save(Restaurant restaurant);

    // TODO: need to delete everything below this
    List<RestaurantEntity> findAll();
    List<RestaurantEntity> findByStatus(String status);
    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    List<RestaurantEntity> findByStatusAndRestaurantPosition(String status, String restaurantPosition);
    List<RestaurantEntity> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status,String restaurantPosition);
    RestaurantEntity findByRestaurantId(Integer id);

    // 페이징

    List<RestaurantEntity> findByStatusAndMainTierNot(String status, Integer mainTier);

    List<RestaurantEntity> findByStatusAndRestaurantPositionAndMainTierNot(String status, String location, Integer mainTier);

    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndMainTierNot(String cuisine, String status, Integer mainTier);

    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPositionAndMainTierNot(String cuisine, String status, String location, Integer mainTier);

    void save(RestaurantEntity restaurant);

    Optional<RestaurantEntity> findById(Integer id);
}
