package com.kustaurant.kustaurant.common.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Situation {
    private Integer situationId;
    private String situationName;
}
