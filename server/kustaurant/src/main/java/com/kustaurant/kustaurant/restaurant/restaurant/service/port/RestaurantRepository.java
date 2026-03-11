package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant getByIdAndStatus(Long id, String status);
    void increaseVisitCount(Long restaurantId);

    // insta crawl용
    List<RestaurantPhoneMatch> findIdsByPhoneNumbers(Collection<String> phoneNumbers);

    record RestaurantPhoneMatch(
            Long id,
            String phoneNumber
    ) {}
}
