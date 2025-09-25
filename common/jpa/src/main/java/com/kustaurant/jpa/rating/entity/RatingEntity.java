package com.kustaurant.jpa.rating.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
    private long restaurantId;
    @Column(nullable = false)
    private double score;
    @Column(nullable = false)
    private int tier;
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isTemp;
    @Column(nullable = false)
    private LocalDateTime ratedAt;
}
