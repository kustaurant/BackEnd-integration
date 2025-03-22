package com.kustaurant.kustaurant.common.restaurant.infrastructure.situation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "restaurant_situation_relations_tbl")
public class RestaurantSituationRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationId;

    private Integer dataCount;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="situation_id")
    private Situation situation;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    public RestaurantSituationRelation(Integer dataCount, Situation situation, RestaurantEntity restaurant) {
        this.dataCount = dataCount;
        this.situation = situation;
        this.restaurant = restaurant;
    }
}
