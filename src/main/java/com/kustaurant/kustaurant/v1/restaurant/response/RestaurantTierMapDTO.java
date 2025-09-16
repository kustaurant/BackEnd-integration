package com.kustaurant.kustaurant.v1.restaurant.response;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO.ZoomAndRestaurants;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.Coordinate;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.MapConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "restaurant tier map entity")
public class RestaurantTierMapDTO {
    @Schema(description = "지도의 최소 줌입니다. (활용이 힘들다면 말씀해주세요.")
    Integer minZoom = MapConstants.MIN_ZOOM;

    @Schema(description = "즐겨찾기를 한 식당 리스트입니다.")
    List<RestaurantTierDTO> favoriteRestaurants = new ArrayList<>();
    @Schema(description = "티어가 있는 식당 리스트입니다. 이 리스트의 식당은 지도에 항상 표시됩니다.")
    List<RestaurantTierDTO> tieredRestaurants = new ArrayList<>();
    @Schema(description = "평가 수의 부족으로 티어가 없는 식당 리스트입니다. 줌 별로 표시되는 식당 리스트가 주어집니다. 현재 지도의 줌보다 같거나 작은 줌의 식당을 표시하시면 됩니다.")
    List<ZoomAndRestaurants> nonTieredRestaurants = new ArrayList<>();

    @Schema(description = "실선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<Coordinate>> solidPolygonCoordsList = new ArrayList<>();
    @Schema(description = "점선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<Coordinate>> dashedPolygonCoordsList = new ArrayList<>();

    @Schema(description = "지도에 보여야 하는 위도와 경도 범위 입니다. (순서대로 최소 위도/최대 위도/최소 경도/ 최대 경도)")
    List<Double> visibleBounds;


    public static RestaurantTierMapDTO fromV2(
            com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO v2) {
        return new RestaurantTierMapDTO(
                v2.getMinZoom(),
                v2.getFavoriteRestaurants().stream().map(RestaurantTierDTO::fromV2).toList(),
                v2.getTieredRestaurants().stream().map(RestaurantTierDTO::fromV2).toList(),
                v2.getNonTieredRestaurants(),
                v2.getSolidPolygonCoordsList(),
                v2.getDashedPolygonCoordsList(),
                v2.getVisibleBounds()
        );
    }
}
