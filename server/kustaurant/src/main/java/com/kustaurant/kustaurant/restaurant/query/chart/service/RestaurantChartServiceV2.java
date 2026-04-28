package com.kustaurant.kustaurant.restaurant.query.chart.service;

import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.restaurant.query.common.dto.*;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCoreInfoRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import com.kustaurant.map.MapConstantsV2;
import com.kustaurant.map.ZonePolygon;
import com.kustaurant.map.utils.PolygonUtils;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantChartServiceV2 {

    private final RestaurantChartRepository restaurantChartRepository;
    private final RestaurantCoreInfoRepository restaurantCoreInfoRepository;

    private final ChartRankingAssembler chartRankingAssembler;

    // 조건에 맞는 식당 리스트를 반환
    @Cacheable(
            cacheNames = "restaurantChartPageV2",
            key = "#condition.cacheKey()",
            unless = "#result == null || #result.getContent().isEmpty()"
    )
    @Observed
    public Page<RestaurantBaseInfoDtoV2> findBasePage(ChartCondition condition) {
        Page<Long> ids = restaurantChartRepository.getRestaurantIdsWithPage(condition);
        List<RestaurantBaseInfoDtoV2> content = restaurantCoreInfoRepository.getRestaurantTiersBaseV2(ids.getContent());

        if (condition.pageable().isPaged()) setRanking(condition, content);

        return new PageImpl<>(content, condition.pageable(), ids.getTotalElements());
    }

    @Observed
    public Page<RestaurantCoreInfoDtoV2> findByConditions(ChartCondition condition, Long userId) {
        Page<RestaurantBaseInfoDtoV2> basePage = findBasePage(condition);

        List<Long> ids = basePage.getContent().stream().map(RestaurantBaseInfoDtoV2::getRestaurantId).toList();

        Set<Long> eval = (userId == null) ? Set.of() : restaurantCoreInfoRepository.findUserEvaluatedIds(userId, ids);
        Set<Long> fav  = (userId == null) ? Set.of() : restaurantCoreInfoRepository.findUserFavoriteIds(userId, ids);

        List<RestaurantCoreInfoDtoV2> merged = basePage.getContent().stream()
                .map(b -> new RestaurantCoreInfoDtoV2(
                        b,
                        eval.contains(b.getRestaurantId()),
                        fav.contains(b.getRestaurantId())
                        )
                )
                .toList();

        return new PageImpl<>(merged, basePage.getPageable(), basePage.getTotalElements());
    }

    private void setRanking(ChartCondition condition, List<RestaurantBaseInfoDtoV2> content) {
        if (condition.pageable() != null) {
            Pageable pageable = condition.pageable();
            int startRanking = pageable.getPageSize() * pageable.getPageNumber() + 1;
            chartRankingAssembler.enrichDtoListWithRankingV2(content, startRanking);
        }
    }

    // 지도 식당 데이터 반환
    @Observed
    public RestaurantTierMapDTOV2 getRestaurantTierMapDto(
            ChartCondition condition, @Nullable Long userId
    ) {
        condition = condition.removePaging();
        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<RestaurantCoreInfoDtoV2> tieredRestaurantCoreInfos = findByConditions(
                condition.changeTierFilter(TierFilter.WITH_TIER), userId).toList();
        List<RestaurantCoreInfoDtoV2> nonTieredRestaurantCoreInfos = findByConditions(
                condition.changeTierFilter(TierFilter.WITHOUT_TIER), userId).toList();

        // 2. 응답 생성하기
        RestaurantTierMapDTOV2 response = new RestaurantTierMapDTOV2();
        // 2.1 즐겨찾기 리스트
        List<RestaurantCoreInfoDtoV2> favorites = new ArrayList<>();
        if (userId != null) {
            for (RestaurantCoreInfoDtoV2 coreInfo : tieredRestaurantCoreInfos) {
                if (coreInfo.getIsFavorite()) {
                    favorites.add(coreInfo);
                }
            }
        }
        response.setFavoriteRestaurants(favorites);
        // 2.2 티어가 있는 식당 리스트
        response.setTieredRestaurants(tieredRestaurantCoreInfos);
        // 2.3 티어가 없는 식당 리스트
        List<RestaurantCoreInfoDtoV2> nonTier16 = IntStream.range(0, nonTieredRestaurantCoreInfos.size())
                .filter(i ->  i % 4 == 0)
                .mapToObj(nonTieredRestaurantCoreInfos::get)
                .toList();
        List<RestaurantCoreInfoDtoV2> nonTier17 = IntStream.range(0, nonTieredRestaurantCoreInfos.size())
                .filter(i ->  i % 4 == 1)
                .mapToObj(nonTieredRestaurantCoreInfos::get)
                .toList();
        List<RestaurantCoreInfoDtoV2> nonTier18 = IntStream.range(0, nonTieredRestaurantCoreInfos.size())
                .filter(i ->  i % 4 == 2)
                .mapToObj(nonTieredRestaurantCoreInfos::get)
                .toList();
        List<RestaurantCoreInfoDtoV2> nonTier19 = IntStream.range(0, nonTieredRestaurantCoreInfos.size())
                .filter(i ->  i % 4 == 3)
                .mapToObj(nonTieredRestaurantCoreInfos::get)
                .toList();
        response.insertZoomAndRestaurants(16, nonTier16);
        response.insertZoomAndRestaurants(17, nonTier17);
        response.insertZoomAndRestaurants(18, nonTier18);
        response.insertZoomAndRestaurants(19, nonTier19);
        // 2.4 폴리곤 좌표 리스트의 리스트
        if (condition.positions() != null && !condition.positions().contains(("ALL"))) {
            try {
                for (int i = 0; i < MapConstantsV2.ZONES.size(); i++) {
                    Position position = Position.valueOf("L" + (i + 1));
                    if (condition.positions().contains(position.getValue())) {
                        response.getSolidPolygonCoordsList().add(MapConstantsV2.ZONES.get(i).coordinates());
                    } else {
                        response.getDashedPolygonCoordsList().add(MapConstantsV2.ZONES.get(i).coordinates());
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
            }
        } else {
            response.setSolidPolygonCoordsList(MapConstantsV2.ZONES.stream()
                    .map(ZonePolygon::coordinates)
                    .toList());
        }
        // 2.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(PolygonUtils.getBoundingBoxFromPolygons(response.getSolidPolygonCoordsList()));

        return response;
    }
}
