package com.kustaurant.kustaurant.common.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Restaurant restaurant;

    public RestaurantSituationRelation(Integer dataCount, Situation situation, Restaurant restaurant) {
        this.dataCount = dataCount;
        this.situation = situation;
        this.restaurant = restaurant;
    }
}
