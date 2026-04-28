package com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure;

import com.kustaurant.restaurant.entity.RestaurantMenuEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantMenuJpaRepository extends JpaRepository<RestaurantMenuEntity, Integer> {

    List<RestaurantMenuEntity> findAllByRestaurantIdIn(Collection<Long> restaurantIds);

    void deleteByRestaurantId(Long restaurantId);
}
