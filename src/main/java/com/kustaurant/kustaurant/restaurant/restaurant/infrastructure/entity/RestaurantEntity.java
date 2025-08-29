package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Coordinates;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Cuisine;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.GeoPosition;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalDateTime;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties({"createdAt", "updatedAt"})
@Table(name = "restaurants_tbl")
public class RestaurantEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    private String restaurantName;
    private String restaurantType;
    private String restaurantPosition;
    private String restaurantAddress;
    private String restaurantTel;
    @Column(unique = true)
    private String restaurantUrl;
    private String restaurantImgUrl;
    private Integer visitCount;

    private String restaurantCuisine;
    private Double latitude;
    private Double longitude;
    private String partnershipInfo;

    private String status;

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

    public Restaurant toModel() {
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName(restaurantName)
                .restaurantCuisine(Cuisine.find(restaurantCuisine))
                .geoPosition(new GeoPosition(
                        Position.find(restaurantPosition), new Coordinates(latitude, longitude)
                ))
                .restaurantType(restaurantType)
                .restaurantAddress(restaurantAddress)
                .restaurantTel(restaurantTel)
                .restaurantUrl(restaurantUrl)
                .restaurantImgUrl(restaurantImgUrl)
                .partnershipInfo(partnershipInfo)
                .status(status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .visitCount(visitCount)
                .build();
    }
}