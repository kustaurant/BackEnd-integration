package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Situation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.SituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;

    public Situation getSituation(Integer id){
        Optional<Situation> situation = situationRepository.findById(id);
        return situation.orElse(null);
    }
}
