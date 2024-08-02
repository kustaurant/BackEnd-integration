package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.common.exception.DataNotFoundException;
import com.kustaurant.restauranttier.tab3_tier.entity.Situation;
import com.kustaurant.restauranttier.tab3_tier.repository.SituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;

    public Situation getSituation(Integer id){
        Optional<Situation> situation = situationRepository.findById(id);
        if(situation.isPresent()){
            return situation.get();
        }
        else{
            throw  new DataNotFoundException("Situation not found");
        }
    }
}
