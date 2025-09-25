package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import com.querydsl.core.annotations.QueryEmbeddable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@QueryEmbeddable
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class EvaluationSituationId implements Serializable {
    @Column(name = "evaluation_id")
    private Long evaluationId;

    @Column(name = "situation_id")
    private Long situationId;
}