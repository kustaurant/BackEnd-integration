package com.kustaurant.kustaurant.restaurant.query.common.dto;

import com.kustaurant.map.BoundingBox;
import com.kustaurant.map.CoordinateV2;
import com.kustaurant.map.MapConstantsV2;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "2026.04.03")
public class RestaurantTierMapDTOV2 {
    @Schema(description = "지도의 최소 줌입니다. (활용이 힘들다면 말씀해주세요.")
    Integer minZoom = MapConstantsV2.MIN_ZOOM;

    @Schema(description = "즐겨찾기를 한 식당 리스트입니다.")
    List<RestaurantCoreInfoDtoV2> favoriteRestaurants = new ArrayList<>();
    @Schema(description = "티어가 있는 식당 리스트입니다. 이 리스트의 식당은 지도에 항상 표시됩니다.")
    List<RestaurantCoreInfoDtoV2> tieredRestaurants = new ArrayList<>();
    @Schema(description = "평가 수의 부족으로 티어가 없는 식당 리스트입니다. 줌 별로 표시되는 식당 리스트가 주어집니다. 현재 지도의 줌보다 같거나 작은 줌의 식당을 표시하시면 됩니다.")
    List<ZoomAndRestaurants> nonTieredRestaurants = new ArrayList<>();

    @Schema(description = "실선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<CoordinateV2>> solidPolygonCoordsList = new ArrayList<>();
    @Schema(description = "점선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<CoordinateV2>> dashedPolygonCoordsList = new ArrayList<>();

    @Schema(description = "지도에 보여야 하는 위도와 경도 범위 입니다. (순서대로 최소 위도/최대 위도/최소 경도/ 최대 경도)")
    BoundingBox visibleBounds;

    @Data
    @AllArgsConstructor
    public static class ZoomAndRestaurants {
        @Schema(example = "17")
        private Integer zoom;
        private List<RestaurantCoreInfoDtoV2> restaurants;
    }

    public void insertZoomAndRestaurants(Integer zoom, List<RestaurantCoreInfoDtoV2> restaurants) {
        if (this.nonTieredRestaurants == null) {
            this.nonTieredRestaurants = new ArrayList<>();
        }
        this.nonTieredRestaurants.add(new ZoomAndRestaurants(zoom, restaurants));
    }
}
