package com.kustaurant.kustaurant.restaurant.tier.infrastructure;

import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.tier.infrastructure.query.RestaurantChartQuery;
import com.kustaurant.kustaurant.restaurant.tier.service.port.ChartCondition;
import com.kustaurant.kustaurant.restaurant.tier.service.port.RestaurantChartRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantChartRepositoryImpl implements RestaurantChartRepository {

    private final RestaurantChartQuery restaurantChartQuery;

    @Override
    public Page<RestaurantTierDTO> findByCondition(ChartCondition condition, Pageable pageable, Long userId) {
        // 정렬해서 페이지에 맞는 식당 id만 읽어오기
        Page<Integer> ids = restaurantChartQuery.getRestaurantIds(condition, pageable);
        // 식당 데이터(평가 여부, 즐찾 여부, 상황 리스트) 처리하기
        List<RestaurantTierDTO> content = restaurantChartQuery.getRestaurantTiers(
                ids.getContent(), userId);
        // 페이징 변환
        return new PageImpl<>(content, pageable, ids.getTotalElements());
    }
}
