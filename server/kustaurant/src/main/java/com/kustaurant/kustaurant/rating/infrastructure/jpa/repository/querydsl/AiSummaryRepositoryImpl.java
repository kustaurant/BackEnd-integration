package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.jpa.rating.entity.QAiSummaryEntity.aiSummaryEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.kustaurant.jpa.rating.entity.QAiSummaryEntity;
import com.kustaurant.jpa.rating.entity.QRatingEntity;
import com.kustaurant.jpa.rating.repository.RatingJpaRepository;
import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.QAiEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import com.kustaurant.kustaurant.rating.domain.vo.QGlobalStats;
import com.kustaurant.kustaurant.rating.service.port.AiSummaryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AiSummaryRepositoryImpl implements AiSummaryRepository {

    private final JPAQueryFactory queryFactory;
    private final RatingJpaRepository ratingJpaRepository;

    @Override
    public Map<Long, AiEvaluation> getAiEvaluations() {
        List<AiEvaluation> list = queryFactory
                .select(new QAiEvaluation(
                        aiSummaryEntity.restaurantId,
                        aiSummaryEntity.positiveReviewCount.castToNum(Double.class)
                                .divide(aiSummaryEntity.reviewCount.castToNum(Double.class)),
                        aiSummaryEntity.negativeReviewCount.castToNum(Double.class)
                                .divide(aiSummaryEntity.reviewCount.castToNum(Double.class)),
                        aiSummaryEntity.avgScore
                ))
                .from(aiSummaryEntity)
                .where(aiSummaryEntity.reviewCount.goe(1))
                .fetch();

        return list.stream()
                .collect(Collectors.toMap(AiEvaluation::restaurantId, Function.identity()));
    }

    @Override
    public GlobalStats getGlobalStats() {
        QAiSummaryEntity rFrom = new QAiSummaryEntity("rFrom");
        QAiSummaryEntity rAi = new QAiSummaryEntity("rAi");
        QAiSummaryEntity rPos = new QAiSummaryEntity("rPos");
        QAiSummaryEntity rNeg = new QAiSummaryEntity("rNeg");

        Long count = queryFactory
                .select(Wildcard.count)
                .from(rFrom)
                .where(ratingValidAiCondition(rFrom))
                .fetchOne();

        // 자체 평가
        JPQLQuery<Double> meanSelf = JPAExpressions
                .select(evaluationEntity.evaluationScore.avg())
                .from(evaluationEntity);
        JPQLQuery<Double> stdSelf = JPAExpressions
                .select(numberTemplate(Double.class, "stddev_samp({0})", evaluationEntity.evaluationScore))
                .from(evaluationEntity);
        // AI 평가
        JPQLQuery<Double> meanAi = JPAExpressions.select(rAi.avgScore.avg())
                .from(rAi)
                .where(ratingValidAiCondition(rAi));
        JPQLQuery<Double> stdAi = JPAExpressions.select(
                        numberTemplate(Double.class, "stddev_samp({0})", rAi.avgScore))
                .from(rAi)
                .where(ratingValidAiCondition(rAi));
        // 긍정 비율
        JPQLQuery<Double> meanPos = JPAExpressions
                .select(rPos.positiveReviewCount.castToNum(Double.class)
                        .divide(rPos.reviewCount.castToNum(Double.class)).avg())
                .from(rPos)
                .where(ratingValidAiCondition(rPos));
        JPQLQuery<Double> stdPos = JPAExpressions
                .select(numberTemplate(Double.class, "stddev_samp({0})",
                        rPos.positiveReviewCount.castToNum(Double.class)
                                .divide(rPos.reviewCount.castToNum(Double.class))))
                .from(rPos)
                .where(ratingValidAiCondition(rPos));
        // 부정 비율
        JPQLQuery<Double> meanNeg = JPAExpressions
                .select(rNeg.negativeReviewCount.castToNum(Double.class)
                        .divide(rNeg.reviewCount.castToNum(Double.class)).avg())
                .from(rNeg)
                .where(ratingValidAiCondition(rNeg));
        JPQLQuery<Double> stdNeg = JPAExpressions
                .select(numberTemplate(Double.class, "stddev_samp({0})",
                        rNeg.negativeReviewCount.castToNum(Double.class)
                                .divide(rNeg.reviewCount.castToNum(Double.class))))
                .from(rNeg)
                .where(ratingValidAiCondition(rNeg));

        if (count == null || count < 2) {
            return queryFactory
                    .select(new QGlobalStats(
                            meanSelf, stdSelf,
                            Expressions.nullExpression(), Expressions.nullExpression(),
                            Expressions.nullExpression(), Expressions.nullExpression(),
                            Expressions.nullExpression(), Expressions.nullExpression()
                    ))
                    .from(restaurantEntity)
                    .limit(1)
                    .fetchOne();
        }

        return queryFactory
                .select(new QGlobalStats(
                        meanSelf, stdSelf,
                        meanAi, stdAi,
                        meanPos, stdPos,
                        meanNeg, stdNeg
                ))
                .from(rFrom)
                .limit(1)
                .fetchOne();
    }

    private BooleanExpression ratingValidAiCondition(QAiSummaryEntity s) {
        return s.reviewCount.goe(1);
    }
}
