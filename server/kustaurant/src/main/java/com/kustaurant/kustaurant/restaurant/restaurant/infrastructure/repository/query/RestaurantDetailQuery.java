package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.query;

import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.kustaurant.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.restaurant.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.restaurant.entity.QRestaurantMenuEntity.restaurantMenuEntity;
import static com.kustaurant.restaurant.entity.QRestaurantPartnershipEntity.restaurantPartnershipEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository.RestaurantCommonExpressions;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.QRestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.QRestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.QRestaurantDetailV2;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetailV2;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.observation.annotation.Observed;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantDetailQuery {

    private final JPAQueryFactory queryFactory;
    private final RestaurantCommonExpressions restaurantCommonExpressions;

    @Observed
    public Optional<RestaurantDetail> getRestaurantDetails(Long restaurantId, Long userId) {
        JPAQuery<?> q = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantMenuEntity)
                .on(menuRestaurantIdEq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(restaurantCommonExpressions.situationMatches(restaurantSituationRelationEntity, restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(evaluationEntity)
                .on(evaluationRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantFavoriteEntity)
                .on(favoriteRestaurantIdEq(restaurantEntity.restaurantId, userId));

        Map<Long, RestaurantDetail> result = q.where(restaurantIdEq(restaurantId))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantDetail(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantImgUrl,
                                        ratingEntity.tier.coalesce(0),
                                        ratingEntity.isTemp,
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantAddress,
                                        naverMapUrl(),
                                        set(situationEntity.situationName),
                                        restaurantEntity.partnershipInfo,
                                        Expressions.constant(getEvaluationCount(restaurantId)),
                                        numberTemplate(Double.class, "ROUND({0}, 2)", ratingEntity.finalScore.coalesce(0.0)),
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        Expressions.constant(getFavoriteCount(restaurantId)),
                                        set(new QRestaurantMenu(
                                                restaurantMenuEntity.id,
                                                restaurantMenuEntity.restaurantId,
                                                restaurantMenuEntity.menuName,
                                                restaurantMenuEntity.menuPrice,
                                                restaurantMenuEntity.menuImgUrl
                                        )),
                                        restaurantEntity.restaurantType,
                                        restaurantEntity.restaurantTel,
                                        restaurantEntity.visitCount,
                                        restaurantEntity.latitude,
                                        restaurantEntity.longitude
                                )
                        )
                );
        if (!result.containsKey(restaurantId)) {
            return Optional.empty();
        }

        return Optional.of(result.get(restaurantId));
    }

    @Observed
    public Optional<RestaurantDetailV2> getRestaurantDetailsV2(Long restaurantId, Long userId) {
        JPAQuery<?> q = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantMenuEntity)
                .on(menuRestaurantIdEq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(restaurantCommonExpressions.situationMatches(restaurantSituationRelationEntity, restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(evaluationEntity)
                .on(evaluationRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantFavoriteEntity)
                .on(favoriteRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantPartnershipEntity)
                .on(restaurantPartnershipEntity.restaurantId.eq(restaurantEntity.restaurantId));

        Map<Long, RestaurantDetailV2> result = q.where(restaurantIdEq(restaurantId))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantDetailV2(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantImgUrl,
                                        ratingEntity.tier.coalesce(0),
                                        ratingEntity.isTemp,
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantAddress,
                                        naverMapUrl(),
                                        set(situationEntity.situationName),
                                        set(partnershipText()),
                                        Expressions.constant(getEvaluationCount(restaurantId)),
                                        numberTemplate(Double.class, "ROUND({0}, 2)", ratingEntity.finalScore.coalesce(0.0)),
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull(),
                                        Expressions.constant(getFavoriteCount(restaurantId)),
                                        set(new QRestaurantMenu(
                                                restaurantMenuEntity.id,
                                                restaurantMenuEntity.restaurantId,
                                                restaurantMenuEntity.menuName,
                                                restaurantMenuEntity.menuPrice,
                                                restaurantMenuEntity.menuImgUrl
                                        )),
                                        restaurantEntity.restaurantType,
                                        restaurantEntity.restaurantTel,
                                        restaurantEntity.visitCount,
                                        restaurantEntity.latitude,
                                        restaurantEntity.longitude
                                )
                        )
                );
        if (!result.containsKey(restaurantId)) {
            return Optional.empty();
        }

        return Optional.of(result.get(restaurantId));
    }

    public Long getFavoriteCount(Long restaurantId) {
        Long favoriteCount = queryFactory
                .select(restaurantFavoriteEntity.id.count())
                .from(restaurantFavoriteEntity)
                .where(restaurantFavoriteEntity.restaurantId.eq(restaurantId))
                .fetchOne();
        if (isNull(favoriteCount)) {
            favoriteCount = 0L;
        }
        return favoriteCount;
    }

    public Integer getEvaluationCount(Long restaurantId) {
        Long evaluationCount = queryFactory
                .select(evaluationEntity.id.count())
                .from(evaluationEntity)
                .where(evaluationEntity.restaurantId.eq(restaurantId))
                .fetchOne();
        if (isNull(evaluationCount)) {
            evaluationCount = 0L;
        }
        return evaluationCount.intValue();
    }

    private BooleanExpression restaurantIdEq(Long restaurantId) {
        return restaurantEntity.restaurantId.eq(restaurantId);
    }

    private BooleanExpression menuRestaurantIdEq(NumberPath<Long> restaurantId) {
        return restaurantMenuEntity.restaurantId.eq(restaurantId);
    }

    private BooleanExpression evaluationRestaurantIdEq(NumberPath<Long> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : evaluationEntity.restaurantId.eq(restaurantId).and(evaluationEntity.userId.eq(userId));
    }

    private BooleanExpression favoriteRestaurantIdEq(NumberPath<Long> restaurantId, Long userId) {
        return isNull(userId) ? Expressions.FALSE
                : restaurantFavoriteEntity.restaurantId.eq(restaurantId)
                        .and(restaurantFavoriteEntity.userId.eq(userId))
                        .and(restaurantFavoriteEntity.status.eq("ACTIVE"));
    }

    private BooleanExpression situationIdEq(NumberPath<Long> situationId) {
        return isNull(situationId) ? Expressions.FALSE : situationEntity.situationId.eq(situationId);
    }

    private Expression<String> naverMapUrl() {
        return Expressions.stringTemplate(
                "concat('https://map.naver.com/p/entry/place/', {0})",
                restaurantEntity.placeId
        );
    }

    private Expression<String> partnershipText() {
        return Expressions.stringTemplate(
                "concat('대상: ', {0}, ', 내용: ', {1})",
                Expressions.stringTemplate(
                        "case " +
                                "when {0} = 'ALL' then '재학생모두' " +
                                "when {0} = 'ENGINEERING' then '공과대학' " +
                                "when {0} = 'EDUCATION' then '사범대학' " +
                                "when {0} = 'SOCIAL_SCIENCE' then '사회과학대학' " +
                                "when {0} = 'LIFE_SCIENCE' then '생명과학대학' " +
                                "when {0} = 'VETERINARY' then '수의과대학' " +
                                "when {0} = 'ART_DESIGN' then '예술디자인대학' " +
                                "when {0} = 'CONVERGENCE_SCI_TECH' then '융합과학기술원' " +
                                "when {0} = 'SCIENCE' then '이과대학' " +
                                "when {0} = 'CLUBUNION' then '동아리연합' " +
                                "when {0} = 'WELFARE' then '학생복지위원회' " +
                                "else {0} end",
                        restaurantPartnershipEntity.target
                ),
                restaurantPartnershipEntity.benefit
        );
    }
}
