package com.kustaurant.kustaurant.common.discovery.service;

import com.kustaurant.kustaurant.common.discovery.domain.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.discovery.service.port.DiscoveryRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscoverySearchService {

    private final DiscoveryRepository discoveryRepository;
    private final DiscoveryAssembler discoveryAssembler;

    public List<RestaurantTierDTO> search(String[] kwList, @Nullable Integer userId) {
        // 검색 결과 가져와서 DTO로 매핑
        List<RestaurantTierDTO> dtoList = discoveryRepository.search(kwList)
                .stream().map(DiscoveryMapper::toDto).toList();

        // 각 식당의 즐찾여부, 평가여부, 랭킹 정보 채우기
        discoveryAssembler.enrichDtoList(userId, dtoList, null);

        return dtoList;
    }
}
