package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity;

import com.kustaurant.mainapp.common.infrastructure.BaseTimeEntity;
import com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantFavorite;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLDelete(sql = "update restaurant_favorite set status = 'DELETED' where favorite_id = ?")
@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="restaurant_favorite")
public class RestaurantFavoriteEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

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
