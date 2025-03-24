package com.kustaurant.kustaurant.common.restaurant.infrastructure.hashtag;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@Table(name="restaurant_hashtags_tbl")
public class RestaurantHashtagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hashtagId;

    private String hashtagName;

    @ManyToMany(mappedBy = "restaurantHashtagList")
    private List<RestaurantEntity> restaurantList = new ArrayList<>();

}
