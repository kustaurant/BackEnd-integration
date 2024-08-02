package com.kustaurant.restauranttier.tab3_tier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "지도 상의 좌표를 나타냅니다.")
@AllArgsConstructor
public class Coordinate {
    @Schema(description = "경도")
    private Double x;
    @Schema(description = "위도")
    private Double y;
}
