package com.kustaurant.kustaurant.restaurant.tier;


import com.fasterxml.jackson.annotation.JsonIgnore;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.RestaurantSituationRelationEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RestaurantTierDataClass {
    private String ranking;
    private RestaurantEntity restaurant;

    private Boolean isFavorite = false;
    private Boolean isEvaluation = false;
    @JsonIgnore
    private List<RestaurantSituationRelationEntity> restaurantSituationRelationEntityList = new ArrayList<>();

    public RestaurantTierDataClass(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public void addSituation(RestaurantSituationRelationEntity restaurantSituationRelationEntity) {
        this.restaurantSituationRelationEntityList.add(restaurantSituationRelationEntity);
    }
}
