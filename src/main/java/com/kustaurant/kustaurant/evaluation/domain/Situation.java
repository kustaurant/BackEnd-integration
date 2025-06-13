package com.kustaurant.kustaurant.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Situation {
    private Integer situationId;
    private String situationName;
}
