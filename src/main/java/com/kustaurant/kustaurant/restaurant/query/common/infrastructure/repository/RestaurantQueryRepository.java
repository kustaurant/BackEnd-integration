package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.RESTAURANT_NOT_FOUND;

import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantChartQuery;
import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantCoreInfoQuery;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantHomeQuery;
import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.query.RestaurantSearchQuery;
import com.kustaurant.kustaurant.restaurant.query.draw.RestaurantDrawRepository;
import com.kustaurant.kustaurant.restaurant.query.home.RestaurantHomeRepository;
import com.kustaurant.kustaurant.restaurant.query.search.RestaurantSearchRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantQueryRepository implements RestaurantChartRepository,
        RestaurantHomeRepository, RestaurantSearchRepository, RestaurantDrawRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;

    private final RestaurantChartQuery restaurantChartQuery;
    private final RestaurantCoreInfoQuery restaurantCoreInfoQuery;

    @Override
    public Page<RestaurantCoreInfoDto> getChartRestaurantsByCondition(ChartCondition condition, Pageable pageable, Long userId) {
        // 정렬해서 페이지에 맞는 식당 id만 읽어오기
        Page<Integer> ids = restaurantChartQuery.getRestaurantIdsWithPage(condition, pageable);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        List<RestaurantCoreInfoDto> content = restaurantCoreInfoQuery.getRestaurantTiers(
                ids.getContent(), userId);
        // 페이징 변환
        return new PageImpl<>(content, pageable, ids.getTotalElements());
    }

    // -------------------------------------------------------

    private final RestaurantHomeQuery restaurantHomeQuery;

    @Override
    public List<RestaurantCoreInfoDto> getTopRestaurants(int size, Long userId) {
        // 식당 id만 읽어오기
        List<Integer> ids = restaurantHomeQuery.getTopRestaurantIds(size);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoQuery.getRestaurantTiers(ids, userId);
    }

    @Override
    public List<RestaurantCoreInfoDto> getRandomRestaurants(int size, Long userId) {
        // 식당 id만 읽어오기
        List<Integer> ids = restaurantHomeQuery.getRandomRestaurantIds(size);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoQuery.getRestaurantTiers(ids, userId);
    }

    // -------------------------------------------------------

    private final RestaurantSearchQuery restaurantSearchQuery;

    @Override
    public List<RestaurantCoreInfoDto> search(String[] kwArr, Long userId, int size) {
        // 식당 id만 읽어오기
        List<Integer> ids = restaurantSearchQuery.searchRestaurantIds(kwArr, size);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoQuery.getRestaurantTiers(ids, userId);
    }

    // -------------------------------------------------------

    @Override
    public Restaurant getById(Integer id) {
        return restaurantJpaRepository.findByRestaurantIdAndStatus(id, "ACTIVE")
                .map(RestaurantEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_NOT_FOUND, id, "식당"));
    }

    @Override
    public List<RestaurantCoreInfoDto> draw(List<String> cuisines, List<String> positions) {
        ChartCondition condition = new ChartCondition(cuisines, null, positions);
        // 정렬해서 페이지에 맞는 식당 id만 읽어오기
        List<Integer> ids = restaurantChartQuery.getRestaurantIds(condition);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        return restaurantCoreInfoQuery.getRestaurantTiers(ids, null);
    }
}
