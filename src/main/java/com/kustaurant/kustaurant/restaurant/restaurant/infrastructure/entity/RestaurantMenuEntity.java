package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="restaurant_menus_tbl")
public class RestaurantMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    RestaurantEntity restaurant;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;

    public RestaurantMenu toDomain() {
        return RestaurantMenu.builder()
                .menuId(this.menuId)
                .menuImgUrl(this.menuImgUrl)
                .menuName(this.menuName)
                .menuPrice(this.menuPrice)
                .naverType(this.naverType)
                .restaurant(this.restaurant.toDomain())
                .build();
    }
}
