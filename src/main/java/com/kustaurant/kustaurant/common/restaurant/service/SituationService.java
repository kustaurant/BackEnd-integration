package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.situation.SituationEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.situation.SituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;

    public SituationEntity getSituation(Integer id){
        Optional<SituationEntity> situation = situationRepository.findById(id);
        return situation.orElse(null);
    }
}
