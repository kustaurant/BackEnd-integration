package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.RestaurantQueryRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.spec.RestaurantSearchSpec;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantQueryRepository, RestaurantRepository {

    private final RestaurantJpaRepository jpaRepository;

    // DiscoveryRepository

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
    public List<Restaurant> search(String[] kwList) {
        Specification<RestaurantEntity> spec = RestaurantSearchSpec.createSearchSpecification(kwList);
        return jpaRepository.findAll(spec)
                .stream().map(RestaurantEntity::toDomain).toList();
    }

    //--------------------------------------------
    // RestaurantRepository
    @Override
    public Restaurant getById(Integer id) {
        return jpaRepository.findById(id).map(RestaurantEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, id, "식당"));
    }

    @Override
    public Restaurant getByIdAndStatus(Integer id, String status) {
        return jpaRepository.findByRestaurantIdAndStatus(id, status).map(RestaurantEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, "요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id + ", status: " + status));
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
    public Restaurant getReference(Integer id) {
        return jpaRepository.getReferenceById(id).toDomain();
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
