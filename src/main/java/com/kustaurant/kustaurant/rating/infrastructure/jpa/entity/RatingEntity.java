package com.kustaurant.kustaurant.rating.infrastructure.jpa.entity;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_rating")
public class RatingEntity {

    @Id
    @Column(name = "restaurant_id")
    private int restaurantId;
    @Column(nullable = false)
    private double score;
    @Column(nullable = false)
    private int tier;

    public static RatingEntity from(Rating rating) {
        return new RatingEntity(
                rating.restaurantId(),
                rating.score(),
                rating.tier().getValue()
        );
    }
}
