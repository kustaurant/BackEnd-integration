package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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
    @ManyToOne
    @JoinColumn(name="user_id")
    UserEntity user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    RestaurantEntity restaurant;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();

        // 연 차이 계산
        Long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference.toString() + "년 전";

        // 월 차이 계산
        Long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference.toString() + "달 전";

        // 일 차이 계산
        Long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference.toString() + "일 전";

        // 시간 차이 계산
        Long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference.toString() + "시간 전";

        // 분 차이 계산
        Long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference.toString() + "분 전";

        // 초 차이 계산
        Long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference.toString() + "초 전";
    }

    public RestaurantFavorite toDomain() {
        return RestaurantFavorite.builder()
                .favoriteId(favoriteId)
                .user(user)
                .restaurant(restaurant.toDomain())
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static RestaurantFavoriteEntity fromDomain(RestaurantFavorite domain) {
        if (domain == null) {
            return null;
        }

        RestaurantFavoriteEntity entity = new RestaurantFavoriteEntity();
        entity.favoriteId = domain.getFavoriteId();
        entity.user = domain.getUser();
        entity.restaurant = RestaurantEntity.fromDomain(domain.getRestaurant());
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();

        return entity;
    }
}
