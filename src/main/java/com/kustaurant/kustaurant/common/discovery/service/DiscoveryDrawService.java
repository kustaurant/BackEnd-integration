package com.kustaurant.kustaurant.common.discovery.service;

import com.kustaurant.kustaurant.common.discovery.domain.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.discovery.infrastructure.DiscoverySpecification;
import com.kustaurant.kustaurant.common.discovery.service.port.DiscoveryRepository;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscoveryDrawService {

    private final int TARGET_SIZE = 30;
    private final DiscoveryRepository discoveryRepository;

    Random rand = new Random();

    public List<RestaurantTierDTO> draw(List<String> cuisines, List<String> locations) {
        List<RestaurantTierDTO> dtoList = discoveryRepository.findAll(
                DiscoverySpecification.withCuisinesAndLocationsAndSituations(cuisines, locations, null, "ACTIVE", null, false))
                .stream().map(DiscoveryMapper::toDto).toList();

        // 조건에 맞는 식당이 없을 경우 404 에러 반환
        if (dtoList.isEmpty()) {
            throw new OptionalNotExistException("해당 조건에 맞는 맛집이 존재하지 않습니다.");
        }

        return pickRandomUnique(dtoList);
    }

    private List<RestaurantTierDTO> pickRandomUnique(List<RestaurantTierDTO> candidates) {
        Collections.shuffle(candidates, rand);

        if (candidates.size() >= TARGET_SIZE) {
            return new ArrayList<>(candidates.subList(0, TARGET_SIZE));
        }

        List<RestaurantTierDTO> result = new ArrayList<>(candidates);
        while (result.size() < TARGET_SIZE) {
            result.add(candidates.get(rand.nextInt(candidates.size())));
        }
        return result;
    }
}
