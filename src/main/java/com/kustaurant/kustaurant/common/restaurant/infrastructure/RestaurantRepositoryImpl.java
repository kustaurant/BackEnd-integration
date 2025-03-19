package com.kustaurant.kustaurant.common.restaurant.infrastructure;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    @Override
    public Optional<RestaurantDomain> findById(Integer id) {
        return jpaRepository.findById(id).map(RestaurantEntity::toModel);
    }

    @Override
    public Optional<RestaurantDomain> findByIdAndStatus(Integer id, String status) {
        return jpaRepository.findByRestaurantIdAndStatus(id, status).map(RestaurantEntity::toModel);
    }

    @Override
    public List<RestaurantDomain> findByStatus(String status) {
        return jpaRepository.findByStatus(status).stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public List<RestaurantDomain> findByCuisineAndStatus(String cuisine, String status) {
        return jpaRepository.findByRestaurantCuisineAndStatus(cuisine, status).stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public List<RestaurantDomain> findByPositionAndStatus(String position, String status) {
        return jpaRepository.findByRestaurantPositionAndStatus(position, status).stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public List<RestaurantDomain> findByCuisineAndPositionAndStatus(String cuisine, String position, String status) {
        return jpaRepository.findByRestaurantCuisineAndRestaurantPositionAndStatus(cuisine, position, status).stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public RestaurantDomain save(RestaurantDomain restaurantDomain) {
        return jpaRepository.save(RestaurantEntity.from(restaurantDomain)).toModel();
    }
}
