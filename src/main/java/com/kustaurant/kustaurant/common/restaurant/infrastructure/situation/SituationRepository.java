package com.kustaurant.kustaurant.common.restaurant.infrastructure.situation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SituationRepository extends JpaRepository<SituationEntity,Integer> {
    SituationEntity findBySituationName(String situationName);

    Optional<SituationEntity> findBySituationId(Integer situationId);
}
