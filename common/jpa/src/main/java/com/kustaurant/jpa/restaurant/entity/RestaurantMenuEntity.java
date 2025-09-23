package com.kustaurant.jpa.restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name="restaurant_menus")
public class RestaurantMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;

}
