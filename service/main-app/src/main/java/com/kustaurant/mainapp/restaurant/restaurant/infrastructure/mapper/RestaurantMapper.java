package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.mapper;

import com.kustaurant.mainapp.restaurant.restaurant.domain.Coordinates;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Cuisine;
import com.kustaurant.mainapp.restaurant.restaurant.domain.GeoPosition;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Position;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.jpa.restaurant.entity.RestaurantEntity;

public class RestaurantMapper {

    public static RestaurantEntity from(Restaurant restaurant) {
        return new RestaurantEntity(
                restaurant.getRestaurantId(),
                restaurant.getRestaurantName(),
                restaurant.getRestaurantType(),
                restaurant.getGeoPosition().position().getValue(),
                restaurant.getRestaurantAddress(),
                restaurant.getRestaurantTel(),
                restaurant.getRestaurantUrl(),
                restaurant.getRestaurantImgUrl(),
                restaurant.getVisitCount(),
                restaurant.getRestaurantCuisine().getValue(),
                restaurant.getGeoPosition().coordinates().latitude(),
                restaurant.getGeoPosition().coordinates().longitude(),
                restaurant.getPartnershipInfo(),
                restaurant.getStatus()
        );
    }

    public static Restaurant toModel(RestaurantEntity entity) {
        return Restaurant.builder()
                .restaurantId(entity.getRestaurantId())
                .restaurantName(entity.getRestaurantName())
                .restaurantCuisine(Cuisine.find(entity.getRestaurantCuisine()))
                .geoPosition(new GeoPosition(
                        Position.find(entity.getRestaurantPosition()), new Coordinates(entity.getLatitude(), entity.getLongitude())
                ))
                .restaurantType(entity.getRestaurantType())
                .restaurantAddress(entity.getRestaurantAddress())
                .restaurantTel(entity.getRestaurantTel())
                .restaurantUrl(entity.getRestaurantUrl())
                .restaurantImgUrl(entity.getRestaurantImgUrl())
                .partnershipInfo(entity.getPartnershipInfo())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .visitCount(entity.getVisitCount())
                .build();
    }
}
