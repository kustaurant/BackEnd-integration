package com.kustaurant.restauranttier.tab3_tier.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@Table(name="restaurant_hashtags_tbl")
public class RestaurantHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hashtagId;

    private String hashtagName;

    @ManyToMany(mappedBy = "restaurantHashtagList")
    private List<Restaurant> restaurantList = new ArrayList<>();

}
