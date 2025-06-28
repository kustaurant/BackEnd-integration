package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
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
public class RestaurantSituationRelationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer relationId;

    private Integer dataCount;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="situation_id")
    private SituationEntity situation;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    public RestaurantSituationRelationEntity(Integer dataCount, SituationEntity situation, RestaurantEntity restaurant) {
        this.dataCount = dataCount;
        this.situation = situation;
        this.restaurant = restaurant;
    }
}
