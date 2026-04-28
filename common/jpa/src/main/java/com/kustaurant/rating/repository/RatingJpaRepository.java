package com.kustaurant.rating.repository;

import com.kustaurant.rating.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {

}
