package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Situation {
    private Long situationId;
    private String situationName;
}
