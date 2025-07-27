package com.kustaurant.kustaurant.restaurant.favorite.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "즐겨찾기 후에 해당 식당에 대한 즐겨찾기 여부(boolean)와 해당 식당의 현재 즐겨찾기 수(int)를 반환합니다.")
public class FavoriteResponseDTO {
    @Schema(description = "즐겨찾기 여부, (true->즐겨찾기 추가됨, false->즐겨찾기 해제됨.)", example = "true")
    private Boolean isFavorite;
    @Schema(description = "해당 식당의 즐겨찾기 수", example = "23")
    private Long count;
}
