package com.kustaurant.kustaurant.common.restaurant.infrastructure.situation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SituationRepository extends JpaRepository<Situation,Integer> {
    Situation findBySituationName(String situationName);

    Optional<Situation> findBySituationId(Integer situationId);
}
