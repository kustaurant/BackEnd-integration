package com.kustaurant.kustaurant.evaluation.evaluation.constants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StarComment {

    @Schema(description = "별점", example = "4.5")
    private final double star;
    @Schema(description = "문구", example = "인생 최고의 식당입니다.")
    private final String comment;
}
