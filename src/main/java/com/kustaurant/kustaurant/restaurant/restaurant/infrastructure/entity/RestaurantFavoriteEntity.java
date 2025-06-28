package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Setter
@Getter
@Table(name="restaurant_favorite_tbl")
public class RestaurantFavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    RestaurantEntity restaurant;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RestaurantFavorite toDomain() {
        return RestaurantFavorite.builder()
                .favoriteId(favoriteId)
                .userId(userId)
                .restaurantId(restaurant.getRestaurantId())
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static RestaurantFavoriteEntity fromDomain(RestaurantFavorite domain, Long userId, Restaurant restaurant) {
        if (domain == null) {
            return null;
        }

        RestaurantFavoriteEntity entity = new RestaurantFavoriteEntity();
        entity.favoriteId = domain.getFavoriteId();
        entity.userId = domain.getUserId();
        entity.restaurant = RestaurantEntity.fromDomain(restaurant);
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();

        return entity;
    }
}
