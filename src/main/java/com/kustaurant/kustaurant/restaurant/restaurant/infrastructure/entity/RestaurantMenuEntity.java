package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public RestaurantMenu toModel() {
        return RestaurantMenu.builder()
                .menuId(this.id)
                .menuImgUrl(this.menuImgUrl)
                .menuName(this.menuName)
                .menuPrice(this.menuPrice)
                .naverType(this.naverType)
                .restaurantId(this.restaurantId)
                .build();
    }
}
