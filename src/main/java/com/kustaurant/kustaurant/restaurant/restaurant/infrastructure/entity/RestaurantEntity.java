package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Coordinates;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Cuisine;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.GeoPosition;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Tier;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties({"createdAt", "updatedAt"})
@Table(name = "restaurants_tbl")
public class RestaurantEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer restaurantId;

    private String restaurantName;
    private String restaurantType;
    private String restaurantPosition;
    private String restaurantAddress;
    private String restaurantTel;
    @Column(unique = true)
    private String restaurantUrl;
    private String restaurantImgUrl;
    private Integer visitCount;
    private Integer restaurantEvaluationCount;
    private Double restaurantScoreSum;
    private Integer mainTier;

    private String restaurantCuisine;
    private Double latitude;
    private Double longitude;
    private String partnershipInfo;

    private String status;

    public void updateStatistics(Restaurant restaurant) {
        this.visitCount = restaurant.getVisitCount();
        this.restaurantEvaluationCount = restaurant.getRestaurantEvaluationCount();
        this.restaurantScoreSum = restaurant.getRestaurantScoreSum();
        this.mainTier = restaurant.getMainTier().getValue();
    }

    public static RestaurantEntity from(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setRestaurantId(restaurant.getRestaurantId());
        entity.setRestaurantName(restaurant.getRestaurantName());
        entity.setRestaurantType(restaurant.getRestaurantType());
        entity.setRestaurantPosition(restaurant.getGeoPosition().position().getValue());
        entity.setRestaurantAddress(restaurant.getRestaurantAddress());
        entity.setRestaurantTel(restaurant.getRestaurantTel());
        entity.setRestaurantUrl(restaurant.getRestaurantUrl());
        entity.setRestaurantImgUrl(restaurant.getRestaurantImgUrl());
        entity.setRestaurantCuisine(restaurant.getRestaurantCuisine().getValue());
        entity.setLongitude(restaurant.getGeoPosition().coordinates().longitude());
        entity.setLatitude(restaurant.getGeoPosition().coordinates().latitude());
        entity.setPartnershipInfo(restaurant.getPartnershipInfo());
        entity.setStatus(restaurant.getStatus());
        entity.setVisitCount(restaurant.getVisitCount());
        entity.setRestaurantEvaluationCount(restaurant.getRestaurantEvaluationCount());
        entity.setRestaurantScoreSum(restaurant.getRestaurantScoreSum());
        entity.setMainTier(restaurant.getMainTier().getValue());

        return entity;
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
                .restaurantEvaluationCount(restaurantEvaluationCount)
                .restaurantScoreSum(restaurantScoreSum)
                .mainTier(Tier.find(mainTier))
                .build();
    }
}