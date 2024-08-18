package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Situation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SituationRepository extends JpaRepository<Situation,Integer> {
    Situation findBySituationName(String situationName);

    Optional<Situation> findBySituationId(Integer situationId);
}
