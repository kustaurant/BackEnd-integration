package com.kustaurant.mainapp.rating.infrastructure.jpa.repository;

import com.kustaurant.mainapp.rating.infrastructure.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingJpaRepository  extends JpaRepository<RatingEntity, Integer> {


}
