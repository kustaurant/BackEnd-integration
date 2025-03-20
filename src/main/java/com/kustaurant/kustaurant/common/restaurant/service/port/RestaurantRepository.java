package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    RestaurantDomain getById(Integer id);
    RestaurantDomain getByIdAndStatus(Integer id, String status);
    List<RestaurantDomain> findByCuisineAndStatus(String cuisine, String status);
    List<RestaurantDomain> findByPositionAndStatus(String position, String status);
    List<RestaurantDomain> findByCuisineAndPositionAndStatus(String cuisine, String position, String status);

    RestaurantDomain save(RestaurantDomain restaurantDomain);

    // TODO: need to delete everything below this
    List<RestaurantEntity> findAll();
    List<RestaurantEntity> findByStatus(String status);
    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    List<RestaurantEntity> findByStatusAndRestaurantPosition(String status, String restaurantPosition);
    List<RestaurantEntity> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status,String restaurantPosition);
    RestaurantEntity findByRestaurantId(Integer id);

    // 페이징
    Page<RestaurantEntity> findAll(Pageable pageable);
    // 검색결과 페이징
    Page<RestaurantEntity> findAll(Specification<RestaurantEntity> spec, Pageable pageable);

    List<RestaurantEntity> findByStatusAndMainTierNot(String status, Integer mainTier);

    List<RestaurantEntity> findByStatusAndRestaurantPositionAndMainTierNot(String status, String location, Integer mainTier);

    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndMainTierNot(String cuisine, String status, Integer mainTier);

    List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPositionAndMainTierNot(String cuisine, String status, String location, Integer mainTier);

    List<RestaurantEntity> findAll(Specification<RestaurantEntity> spec);

    void save(RestaurantEntity restaurant);
}
