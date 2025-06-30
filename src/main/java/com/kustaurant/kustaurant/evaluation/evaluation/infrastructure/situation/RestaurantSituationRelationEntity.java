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
    private Long relationId;

    private Integer dataCount;

    @Column(name = "situation_id")
    private Long situationId;

    @Column(name = "restaurant_id")
    private Integer restaurantId;

    public RestaurantSituationRelationEntity(Integer dataCount, Long situationId, Integer restaurantId) {
        this.dataCount = dataCount;
        this.situationId = situationId;
        this.restaurantId = restaurantId;
    }
}
