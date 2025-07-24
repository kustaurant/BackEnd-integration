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
                .stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public List<Restaurant> findAll(Specification<RestaurantEntity> spec, Pageable pageable) {
        return jpaRepository.findAll(spec, pageable).toList()
                .stream().map(RestaurantEntity::toModel).toList();
    }

    @Override
    public List<Restaurant> search(String[] kwList) {
        Specification<RestaurantEntity> spec = RestaurantSearchSpec.createSearchSpecification(kwList);
        return jpaRepository.findAll(spec)
                .stream().map(RestaurantEntity::toModel).toList();
    }

    //--------------------------------------------
    // RestaurantRepository
    @Override
    public Restaurant getById(Integer id) {
        return jpaRepository.findById(id).map(RestaurantEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, id, "식당"));
    }

    @Override
    public Restaurant getByIdAndStatus(Integer id, String status) {
        return jpaRepository.findByRestaurantIdAndStatus(id, status).map(RestaurantEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, "요청한 restaurant가 존재하지 않습니다. 요청 정보 - id: " + id + ", status: " + status));
    }

    @Override
    public void updateStatistics(Restaurant restaurant) {
        RestaurantEntity entity = jpaRepository.findById(restaurant.getRestaurantId())
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND,
                        restaurant.getRestaurantId(), "식당"));

        entity.updateStatistics(restaurant);
    }
}
