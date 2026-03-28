package com.kustaurant.kustaurant.rating.domain.model;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.rating.domain.model.QAiEvaluation is a Querydsl Projection type for AiEvaluation
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QAiEvaluation extends ConstructorExpression<AiEvaluation> {

    private static final long serialVersionUID = 1915132783L;

    public QAiEvaluation(com.querydsl.core.types.Expression<Long> restaurantId, com.querydsl.core.types.Expression<Double> positiveRatio, com.querydsl.core.types.Expression<Double> negativeRatio, com.querydsl.core.types.Expression<Double> aiScoreAvg) {
        super(AiEvaluation.class, new Class<?>[]{long.class, double.class, double.class, double.class}, restaurantId, positiveRatio, negativeRatio, aiScoreAvg);
    }

}

