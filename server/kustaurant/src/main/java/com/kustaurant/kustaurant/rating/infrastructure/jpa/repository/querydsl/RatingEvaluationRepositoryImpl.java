package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationReactionEntity.evaluationReactionEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationSituationEntity.evaluationSituationEntity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity;
import com.kustaurant.kustaurant.rating.domain.vo.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.vo.QEvaluationWithContext;
import com.kustaurant.kustaurant.rating.service.port.RatingEvaluationRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingEvaluationRepositoryImpl implements RatingEvaluationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, List<EvaluationWithContext>> getEvaluationsByRestaurantIds(
            List<Long> restaurantIds) {
        if (restaurantIds == null || restaurantIds.isEmpty()) {
            return new HashMap<>();
        }

        List<EvaluationWithContext> list = queryFactory
                .select(new QEvaluationWithContext(
                        evaluationEntity.restaurantId,
                        evaluationEntity.evaluationScore,
                        evaluationEntity.updatedAt.coalesce(evaluationEntity.createdAt),
                        existString(evaluationEntity.body),
                        existSituation(),
                        existString(evaluationEntity.imgUrl),
                        reactionScore(),
                        userAvgScore(),
                        userEvalCount()
                        ))
                .from(evaluationEntity)
                .where(evaluationEntity.restaurantId.in(restaurantIds))
                .fetch();

        return list.stream()
                .collect(Collectors.groupingBy(
                        EvaluationWithContext::restaurantId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private Expression<Long> userEvalCount() {
        QEvaluationEntity subEval = new QEvaluationEntity("subEval");
        return JPAExpressions
                .select(subEval.count().coalesce(0L))
                .from(subEval)
                .where(subEval.userId.eq(evaluationEntity.userId)
                        .and(subEval.status.eq("ACTIVE")));
    }

    private Expression<Double> userAvgScore() {
        QEvaluationEntity subEval = new QEvaluationEntity("subEval");
        return JPAExpressions
                .select(subEval.evaluationScore.avg().coalesce(0.0))
                .from(subEval)
                .where(subEval.userId.eq(evaluationEntity.userId)
                        .and(subEval.status.eq("ACTIVE")));
    }

    private BooleanExpression existString(StringPath path) {
        return path.coalesce("").trim().isNotEmpty();
    }

    private Expression<Long> reactionScore() {
        // 1) 좋아요 개수
        NumberExpression<Long> likeCnt = Expressions.numberTemplate(
                Long.class,
                "({0})",
                JPAExpressions
                        .select(evaluationReactionEntity.count())
                        .from(evaluationReactionEntity)
                        .where(
                                evaluationReactionEntity.evaluationId.eq(evaluationEntity.id)
                                        .and(evaluationReactionEntity.reaction.eq(ReactionType.LIKE))
                        )
        );
        // 2) 싫어요 개수
        NumberExpression<Long> dislikeCnt = Expressions.numberTemplate(
                Long.class,
                "({0})",
                JPAExpressions
                        .select(evaluationReactionEntity.count())
                        .from(evaluationReactionEntity)
                        .where(
                                evaluationReactionEntity.evaluationId.eq(evaluationEntity.id)
                                        .and(evaluationReactionEntity.reaction.eq(ReactionType.DISLIKE))
                        )
        );
        // 좋아요 개수 - 싫어요 개수
        return likeCnt.subtract(dislikeCnt).coalesce(0L);
    }

    private BooleanExpression existSituation() {
        return queryFactory
                .selectOne()
                .from(evaluationSituationEntity)
                .where(evaluationSituationEntity.id.evaluationId.eq(evaluationEntity.id))
                .exists();
    }

    @Override
    public double getGlobalAvg() {
        Double avg = queryFactory
                .select(evaluationEntity.evaluationScore.avg().coalesce(0.0))
                .from(evaluationEntity)
                .where(evaluationEntity.status.eq("ACTIVE"))
                .fetchOne();
        return avg == null ? 0 : avg;
    }
}
