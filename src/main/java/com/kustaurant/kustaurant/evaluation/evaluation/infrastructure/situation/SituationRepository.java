package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SituationRepository extends JpaRepository<SituationEntity,Long> {
    SituationEntity findBySituationName(String situationName);

    Optional<SituationEntity> findBySituationId(Long situationId);
}
