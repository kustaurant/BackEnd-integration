package com.kustaurant.restauranttier.tab3_tier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "restaurant tier map entity")
public class RestaurantTierMapDTO {
    @Schema(description = "지도의 최소 줌입니다. (활용이 힘들다면 말씀해주세요.")
    Integer minZoom = 13;

    @Schema(description = "즐겨찾기를 한 식당 리스트입니다.")
    List<RestaurantTierDTO> favoriteRestaurants;
    @Schema(description = "티어가 있는 식당 리스트입니다. 이 리스트의 식당은 지도에 항상 표시됩니다.")
    List<RestaurantTierDTO> tieredRestaurants;
    @Schema(description = "평가 수의 부족으로 티어가 없는 식당 리스트입니다. 줌 별로 표시되는 식당 리스트가 주어집니다. 현재 지도의 줌보다 같거나 작은 줌의 식당을 표시하시면 됩니다.")
    List<ZoomAndRestaurants> nonTieredRestaurants;

    @Schema(description = "실선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<Coordinate>> solidPolygonCoordsList = List.of(
            List.of(new Coordinate(37.5421627,127.071636),
                    new Coordinate(37.5427753,127.0710213),
                    new Coordinate(37.5422156,127.0707644),
                    new Coordinate(37.5441201,127.0651452),
                    new Coordinate(37.5482696,127.0674957),
                    new Coordinate(37.5478196,127.0716092),
                    new Coordinate(37.5472574,127.0740324),
                    new Coordinate(37.5459136,127.0733675))
    );
    @Schema(description = "점선으로 표시되는 폴리곤 좌표 리스트의 리스트입니다.")
    List<List<Coordinate>> dashedPolygonCoordsList = List.of(
            List.of(new Coordinate(37.5401732,127.062852),
                    new Coordinate(37.5378977,127.0696049),
                    new Coordinate(37.5421627,127.071636),
                    new Coordinate(37.5427753,127.0710213),
                    new Coordinate(37.5422156,127.0707644),
                    new Coordinate(37.5441201,127.0651452)),
            List.of(new Coordinate(37.5445367,127.0728555),
                    new Coordinate(37.5444815,127.0731477),
                    new Coordinate(37.5447132,127.0739129),
                    new Coordinate(37.5445797,127.0747749),
                    new Coordinate(37.544736,127.0754595),
                    new Coordinate(37.5445765,127.0755668),
                    new Coordinate(37.5449818,127.0800863),
                    new Coordinate(37.545327,127.0799778),
                    new Coordinate(37.5453925,127.0793721),
                    new Coordinate(37.5458133,127.0773484),
                    new Coordinate(37.547219,127.0741961))
    );

    @Schema(description = "지도에 보여야 하는 위도와 경도 범위 입니다. (순서대로 최소 위도/최대 위도/최소 경도/ 최대 경도)")
    List<Double> visibleBounds = List.of(
            37.5421,
            37.5483,
            127.0651,
            127.0741
    );

    @Data
    @AllArgsConstructor
    static class ZoomAndRestaurants {
        @Schema(example = "17")
        private Integer zoom;
        private List<RestaurantTierDTO> restaurants;
    }

    public void insertZoomAndRestaurants(Integer zoom, List<RestaurantTierDTO> restaurants) {
        if (this.nonTieredRestaurants == null) {
            this.nonTieredRestaurants = new ArrayList<>();
        }
        this.nonTieredRestaurants.add(new ZoomAndRestaurants(zoom, restaurants));
    }
}
