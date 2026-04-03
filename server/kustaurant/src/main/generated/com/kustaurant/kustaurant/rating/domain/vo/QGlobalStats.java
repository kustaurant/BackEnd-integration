package com.kustaurant.kustaurant.rating.domain.vo;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.rating.domain.vo.QGlobalStats is a Querydsl Projection type for GlobalStats
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QGlobalStats extends ConstructorExpression<GlobalStats> {

    private static final long serialVersionUID = 1633964301L;

    public QGlobalStats(com.querydsl.core.types.Expression<Double> meanSelf, com.querydsl.core.types.Expression<Double> stdSelf, com.querydsl.core.types.Expression<Double> meanAi, com.querydsl.core.types.Expression<Double> stdAi, com.querydsl.core.types.Expression<Double> meanPos, com.querydsl.core.types.Expression<Double> stdPos, com.querydsl.core.types.Expression<Double> meanNeg, com.querydsl.core.types.Expression<Double> stdNeg) {
        super(GlobalStats.class, new Class<?>[]{double.class, double.class, double.class, double.class, double.class, double.class, double.class, double.class}, meanSelf, stdSelf, meanAi, stdAi, meanPos, stdPos, meanNeg, stdNeg);
    }

}

