package com.kustaurant.jpa.rating.repository;

import com.kustaurant.jpa.rating.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingJpaRepository  extends JpaRepository<RatingEntity, Integer> {


}
