package com.kustaurant.kustaurant.common.restaurant.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantSituationRelation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RestaurantTierDataClass {
    private String ranking;
    private Restaurant restaurant;

    private Boolean isFavorite = false;
    private Boolean isEvaluation = false;
    @JsonIgnore
    private List<RestaurantSituationRelation> restaurantSituationRelationList = new ArrayList<>();

    public RestaurantTierDataClass(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void addSituation(RestaurantSituationRelation restaurantSituationRelation) {
        this.restaurantSituationRelationList.add(restaurantSituationRelation);
    }
}
