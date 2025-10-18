package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.kustaurant.jpa.rating.entity.QRatingEntity;
import com.kustaurant.jpa.rating.entity.RatingEntity;
import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.QAiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.vo.QGlobalStats;
import com.kustaurant.kustaurant.rating.infrastructure.jpa.mapper.RatingMapper;
import com.kustaurant.jpa.rating.repository.RatingJpaRepository;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import com.kustaurant.kustaurant.rating.service.port.RatingRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {

    private final JPAQueryFactory queryFactory;
    private final RatingJpaRepository ratingJpaRepository;

    @Override
    public void saveAll(List<Rating> rating) {
        List<Long> ids = rating.stream().map(Rating::getRestaurantId).distinct().toList();
        List<RatingEntity> existed = ratingJpaRepository.findAllByIdInForUpdate(ids);
        Map<Long, RatingEntity> existedMap = new HashMap<>();
        for (RatingEntity ratingEntity : existed) {
            existedMap.put(ratingEntity.getRestaurantId(), ratingEntity);
        }

        List<RatingEntity> created = new ArrayList<>();

        for (Rating r : rating) {
            if (existedMap.containsKey(r.getRestaurantId())) {
                existedMap.get(r.getRestaurantId()).updateRatingData(
                        r.getScore(), r.getAiScore(), r.getTier().getValue(), r.isTemp(), r.getRatedAt(), r.getFinalScore()
                );
            } else {
                created.add(RatingMapper.from(r));
            }
        }

        ratingJpaRepository.saveAll(created);
    }

    @Override
    public Map<Long, AiEvaluation> getAiEvaluations() {
        List<AiEvaluation> list = queryFactory
                .select(new QAiEvaluation(
                        ratingEntity.restaurantId,
                        ratingEntity.aiPositiveCount.castToNum(Double.class)
                                .divide(ratingEntity.aiReviewCount.castToNum(Double.class)),
                        ratingEntity.aiNegativeCount.castToNum(Double.class)
                                .divide(ratingEntity.aiReviewCount.castToNum(Double.class)),
                        ratingEntity.aiAvgScore
                ))
                .from(ratingEntity)
                .where(ratingEntity.aiProcessedAt.isNotNull()
                        .and(ratingEntity.aiReviewCount.goe(1)))
                .fetch();

        return list.stream()
                .collect(Collectors.toMap(AiEvaluation::restaurantId, Function.identity()));
    }

    @Override
    public GlobalStats getGlobalStats() {
        QRatingEntity rFrom = new QRatingEntity("rFrom");
        QRatingEntity rAi = new QRatingEntity("rAi");
        QRatingEntity rPos = new QRatingEntity("rPos");
        QRatingEntity rNeg = new QRatingEntity("rNeg");

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
        JPQLQuery<Double> meanAi = JPAExpressions.select(rAi.aiAvgScore.avg())
                .from(rAi)
                .where(ratingValidAiCondition(rAi));
        JPQLQuery<Double> stdAi = JPAExpressions.select(
                        numberTemplate(Double.class, "stddev_samp({0})", rAi.aiAvgScore))
                .from(rAi)
                .where(ratingValidAiCondition(rAi));
        // 긍정 비율
        JPQLQuery<Double> meanPos = JPAExpressions
                .select(rPos.aiPositiveCount.castToNum(Double.class)
                        .divide(rPos.aiReviewCount.castToNum(Double.class)).avg())
                .from(rPos)
                .where(ratingValidAiCondition(rPos));
        JPQLQuery<Double> stdPos = JPAExpressions
                .select(numberTemplate(Double.class, "stddev_samp({0})",
                                rPos.aiPositiveCount.castToNum(Double.class)
                                        .divide(rPos.aiReviewCount.castToNum(Double.class))))
                .from(rPos)
                .where(ratingValidAiCondition(rPos));
        // 부정 비율
        JPQLQuery<Double> meanNeg = JPAExpressions
                .select(rNeg.aiNegativeCount.castToNum(Double.class)
                        .divide(rNeg.aiReviewCount.castToNum(Double.class)).avg())
                .from(rNeg)
                .where(ratingValidAiCondition(rNeg));
        JPQLQuery<Double> stdNeg = JPAExpressions
                .select(numberTemplate(Double.class, "stddev_samp({0})",
                                rNeg.aiNegativeCount.castToNum(Double.class)
                                        .divide(rNeg.aiReviewCount.castToNum(Double.class))))
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
                    .from(rFrom)
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

    private BooleanExpression ratingValidAiCondition(QRatingEntity r) {
        return r.aiProcessedAt.isNotNull()
                .and(r.aiReviewCount.isNotNull())
                .and(r.aiReviewCount.goe(1));
    }
}
