package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Coordinates;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Cuisine;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.GeoPosition;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Tier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants_tbl")
public class RestaurantEntity {
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
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    public void updateStatistics(Restaurant restaurant) {
        this.visitCount = restaurant.getVisitCount();
        this.restaurantEvaluationCount = restaurant.getRestaurantEvaluationCount();
        this.restaurantScoreSum = restaurant.getRestaurantScoreSum();
        this.mainTier = restaurant.getMainTier().getValue();
    }

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
                restaurant.getRestaurantEvaluationCount(),
                restaurant.getRestaurantScoreSum(),
                restaurant.getMainTier().getValue(),
                restaurant.getRestaurantCuisine().getValue(),
                restaurant.getGeoPosition().coordinates().latitude(),
                restaurant.getGeoPosition().coordinates().longitude(),
                restaurant.getPartnershipInfo(),
                restaurant.getStatus(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
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
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .visitCount(visitCount)
                .restaurantEvaluationCount(restaurantEvaluationCount)
                .restaurantScoreSum(restaurantScoreSum)
                .mainTier(Tier.find(mainTier))
                .build();
    }
}