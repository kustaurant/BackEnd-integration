package com.kustaurant.kustaurant.common.discovery.infrastructure;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscoveryJpaRepository extends JpaRepository<RestaurantEntity, Integer> {
    List<RestaurantEntity> findAll(Specification<RestaurantEntity> spec);
    Page<RestaurantEntity> findAll(Specification<RestaurantEntity> spec, Pageable pageable);
}
