package com.kustaurant.restauranttier.tab3_tier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="restaurant_menus_tbl")
public class RestaurantMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer menuId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    Restaurant restaurant;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;
}
