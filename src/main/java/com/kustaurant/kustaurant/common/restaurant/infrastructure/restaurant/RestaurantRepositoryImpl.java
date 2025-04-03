package com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant;

import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    @Override
    public Restaurant getById(Integer id) {
        return jpaRepository.findById(id).map(RestaurantEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException("요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id));
    }

    @Override
    public Restaurant getByIdAndStatus(Integer id, String status) {
        return jpaRepository.findByRestaurantIdAndStatus(id, status).map(RestaurantEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException("요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id + ", status: " + status));
    }

    @Override
    public List<Restaurant> findByCuisineAndStatus(String cuisine, String status) {
        return jpaRepository.findByRestaurantCuisineAndStatus(cuisine, status).stream().map(RestaurantEntity::toDomain).toList();
    }

    @Override
    public List<Restaurant> findByPositionAndStatus(String position, String status) {
        return jpaRepository.findByRestaurantPositionAndStatus(position, status).stream().map(RestaurantEntity::toDomain).toList();
    }

    @Override
    public List<Restaurant> findByCuisineAndPositionAndStatus(String cuisine, String position, String status) {
        return jpaRepository.findByRestaurantCuisineAndRestaurantPositionAndStatus(cuisine, position, status).stream().map(RestaurantEntity::toDomain).toList();
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        return jpaRepository.save(RestaurantEntity.fromDomain(restaurant)).toDomain();
    }

    @Override
    public List<Restaurant> findAll(Specification<RestaurantEntity> spec) {
        return jpaRepository.findAll(spec)
                .stream().map(RestaurantEntity::toDomain).toList();
    }

    @Override
    public List<Restaurant> findAll(Specification<RestaurantEntity> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable).toList()
                .stream().map(RestaurantEntity::toDomain).toList();
    }

    @Override
    public List<RestaurantEntity> findAll() {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByStatus(String status) {
        return List.of();
    }

    @Override
    public Optional<RestaurantEntity> findByRestaurantIdAndStatus(Integer restaurantId, String status) {
        return Optional.empty();
    }

    @Override
    public List<RestaurantEntity> findByStatusAndRestaurantPosition(String status, String restaurantPosition) {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status) {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status, String restaurantPosition) {
        return List.of();
    }

    @Override
    public RestaurantEntity findByRestaurantId(Integer id) {
        return null;
    }

    @Override
    public Page<RestaurantEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<RestaurantEntity> findByStatusAndMainTierNot(String status, Integer mainTier) {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByStatusAndRestaurantPositionAndMainTierNot(String status, String location, Integer mainTier) {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByRestaurantCuisineAndStatusAndMainTierNot(String cuisine, String status, Integer mainTier) {
        return List.of();
    }

    @Override
    public List<RestaurantEntity> findByRestaurantCuisineAndStatusAndRestaurantPositionAndMainTierNot(String cuisine, String status, String location, Integer mainTier) {
        return List.of();
    }

    @Override
    public void save(RestaurantEntity restaurant) {

    }

    @Override
    public Optional<RestaurantEntity> findById(Integer id) {
        return jpaRepository.findById(id);
    }
}
