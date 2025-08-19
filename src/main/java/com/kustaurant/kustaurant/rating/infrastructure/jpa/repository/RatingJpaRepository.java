package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository;

import com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingJpaRepository  extends JpaRepository<RatingEntity, Integer> {


}
