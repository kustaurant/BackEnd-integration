package com.kustaurant.restaurant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
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
    private String menuImgUrl;

    private RestaurantMenuEntity(Long restaurantId, String menuName, String menuPrice, String menuImgUrl) {
        this.restaurantId = restaurantId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuImgUrl = menuImgUrl;
    }

    public static RestaurantMenuEntity of(
            Long restaurantId,
            String menuName,
            String menuPrice,
            String menuImgUrl
    ) {
        return new RestaurantMenuEntity(restaurantId, menuName, menuPrice, menuImgUrl);
    }
}
