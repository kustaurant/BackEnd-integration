package com.kustaurant.kustaurant.restaurant.query.chart.service;

import com.kustaurant.kustaurant.restaurant.restaurant.constants.MapConstants;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantChartService {

    private final RestaurantChartRepository restaurantChartRepository;
    private final ChartRankingAssembler chartRankingAssembler;

    // 조건에 맞는 식당 리스트를 반환
    public Page<RestaurantCoreInfoDto> findByConditions(
            ChartCondition condition, Pageable pageable, @Nullable Long userId
    ) {
        // 조건에 맞는 식당 데이터 가져오기
        Page<RestaurantCoreInfoDto> result = restaurantChartRepository.getChartRestaurantsByCondition(condition,
                pageable, userId);
        // 순위 정보 채우기
        int startRanking = pageable.getPageSize() * pageable.getPageNumber() + 1;
        chartRankingAssembler.enrichDtoList(result.getContent(), startRanking);
        return result;
    }

    // 지도 식당 데이터 반환
    public RestaurantTierMapDTO getRestaurantTierMapDto(
            ChartCondition condition, @Nullable Long userId
    ) {
        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<RestaurantCoreInfoDto> tieredRestaurantCoreInfoDtos = findByConditions(condition, null, userId).toList();
        List<RestaurantCoreInfoDto> nonTieredRestaurantCoreInfoDtos = findByConditions(condition, null, userId).toList();

        // 2. 응답 생성하기
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();
        // 2.1 즐겨찾기 리스트
        // TODO: 나중에 이거 수정해야됨
//        response.setFavoriteRestaurants(
//                restaurantFavoriteService.getFavoriteRestaurantDtoList(userId)
//                        .stream().map(DiscoveryMapper::toDto).toList()
//        );
        // 2.2 티어가 있는 식당 리스트
        response.setTieredRestaurants(tieredRestaurantCoreInfoDtos);
        // 2.3 티어가 없는 식당 리스트
        List<RestaurantCoreInfoDto> nonTier16 = IntStream.range(0, nonTieredRestaurantCoreInfoDtos.size())
                .filter(i ->  i % 4 == 0)
                .mapToObj(nonTieredRestaurantCoreInfoDtos::get)
                .toList();
        List<RestaurantCoreInfoDto> nonTier17 = IntStream.range(0, nonTieredRestaurantCoreInfoDtos.size())
                .filter(i ->  i % 4 == 1)
                .mapToObj(nonTieredRestaurantCoreInfoDtos::get)
                .toList();
        List<RestaurantCoreInfoDto> nonTier18 = IntStream.range(0, nonTieredRestaurantCoreInfoDtos.size())
                .filter(i ->  i % 4 == 2)
                .mapToObj(nonTieredRestaurantCoreInfoDtos::get)
                .toList();
        List<RestaurantCoreInfoDto> nonTier19 = IntStream.range(0, nonTieredRestaurantCoreInfoDtos.size())
                .filter(i ->  i % 4 == 3)
                .mapToObj(nonTieredRestaurantCoreInfoDtos::get)
                .toList();
        response.insertZoomAndRestaurants(16, nonTier16);
        response.insertZoomAndRestaurants(17, nonTier17);
        response.insertZoomAndRestaurants(18, nonTier18);
        response.insertZoomAndRestaurants(19, nonTier19);
        // 2.4 폴리곤 좌표 리스트의 리스트
        if (condition.positions() != null && !condition.positions().contains(("ALL"))) {
            try {
                for (int i = 0; i < MapConstants.LIST_OF_COORD_LIST.size(); i++) {
                    Position position = Position.valueOf("L" + (i + 1));
                    if (condition.positions().contains(position.getValue())) {
                        response.getSolidPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    } else {
                        response.getDashedPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
            }
        } else {
            response.setSolidPolygonCoordsList(MapConstants.LIST_OF_COORD_LIST);
        }
        // 2.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(MapConstants.findMinMaxCoordinates(response.getSolidPolygonCoordsList()));

        return response;
    }
}
