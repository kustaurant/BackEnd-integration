package com.kustaurant.kustaurant.restaurant.favorite.infrastructure;

import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@SQLDelete(sql = "update restaurant_favorite_tbl set status = 'DELETED' where favorite_id = ?")
@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="restaurant_favorite_tbl")
public class RestaurantFavoriteEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    private String status;

    public static RestaurantFavoriteEntity from(RestaurantFavorite domain) {
        if (domain == null) {
            return null;
        }
        RestaurantFavoriteEntity entity = new RestaurantFavoriteEntity();
        entity.id = domain.getId();
        entity.userId = domain.getUserId();
        entity.restaurantId = domain.getRestaurantId();
        entity.status = domain.getStatus();

        return entity;
    }

    public RestaurantFavorite toModel() {
        return RestaurantFavorite.builder()
                .id(id)
                .userId(userId)
                .restaurantId(restaurantId)
                .status(status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
