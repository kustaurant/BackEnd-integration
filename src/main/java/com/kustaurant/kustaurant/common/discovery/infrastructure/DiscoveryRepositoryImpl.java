package com.kustaurant.kustaurant.common.discovery.infrastructure;

import com.kustaurant.kustaurant.common.discovery.service.port.DiscoveryRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DiscoveryRepositoryImpl implements DiscoveryRepository {

    private final DiscoveryJpaRepository jpaRepository;

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
        Specification<RestaurantEntity> spec = DiscoverySearchSpec.createSearchSpecification(kwList);
        return jpaRepository.findAll(spec)
                .stream().map(RestaurantEntity::toDomain).toList();
    }


}
