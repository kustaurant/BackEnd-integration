package com.kustaurant.kustaurant.rating.domain.vo;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.rating.domain.vo.QEvaluationWithContext is a Querydsl Projection type for EvaluationWithContext
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QEvaluationWithContext extends ConstructorExpression<EvaluationWithContext> {

    private static final long serialVersionUID = -2091084226L;

    public QEvaluationWithContext(com.querydsl.core.types.Expression<Long> restaurantId, com.querydsl.core.types.Expression<Double> score, com.querydsl.core.types.Expression<java.time.LocalDateTime> evaluatedAt, com.querydsl.core.types.Expression<Boolean> existComment, com.querydsl.core.types.Expression<Boolean> existSituation, com.querydsl.core.types.Expression<Boolean> existImage, com.querydsl.core.types.Expression<Long> reactionScore, com.querydsl.core.types.Expression<Double> userAvgScore, com.querydsl.core.types.Expression<Long> userEvalCount) {
        super(EvaluationWithContext.class, new Class<?>[]{long.class, double.class, java.time.LocalDateTime.class, boolean.class, boolean.class, boolean.class, long.class, double.class, long.class}, restaurantId, score, evaluatedAt, existComment, existSituation, existImage, reactionScore, userAvgScore, userEvalCount);
    }

}

