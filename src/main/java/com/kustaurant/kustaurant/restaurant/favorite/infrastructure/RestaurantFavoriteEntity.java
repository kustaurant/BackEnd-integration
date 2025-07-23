package com.kustaurant.kustaurant.restaurant.favorite.infrastructure;

import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name="restaurant_favorite_tbl")
public class RestaurantFavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RestaurantFavorite toModel() {
        return RestaurantFavorite.builder()
                .id(id)
                .userId(userId)
                .restaurantId(restaurantId)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static RestaurantFavoriteEntity from(RestaurantFavorite domain) {
        if (domain == null) {
            return null;
        }

        RestaurantFavoriteEntity entity = new RestaurantFavoriteEntity();
        entity.id = domain.getId();
        entity.userId = domain.getUserId();
        entity.restaurantId = domain.getRestaurantId();
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();

        return entity;
    }
}
