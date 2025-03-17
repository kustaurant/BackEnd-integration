package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Situation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SituationRepository extends JpaRepository<Situation,Integer> {
    Situation findBySituationName(String situationName);

    Optional<Situation> findBySituationId(Integer situationId);
}
