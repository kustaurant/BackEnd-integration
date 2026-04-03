package com.kustaurant.kustaurant.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantFavoriteEntity.restaurantFavoriteEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantPartnershipEntity.restaurantPartnershipEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QRestaurantSituationRelationEntity.restaurantSituationRelationEntity;
import static com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.QSituationEntity.situationEntity;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.set;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static java.util.Objects.isNull;

import com.kustaurant.kustaurant.restaurant.query.common.dto.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.micrometer.observation.annotation.Observed;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantCoreInfoRepository {

    private final JPAQueryFactory queryFactory;
    private final RestaurantCommonExpressions restaurantCommonExpressions;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Observed
    public List<RestaurantBaseInfoDto> getRestaurantTiersBase(List<Long> ids) {
        Map<Long, RestaurantBaseInfoDto> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(restaurantCommonExpressions.situationMatches(restaurantSituationRelationEntity, restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .where(restaurantEntity.restaurantId.in(ids))
                .transform(groupBy(restaurantEntity.restaurantId).as(
                        new QRestaurantBaseInfoDto(
                                restaurantEntity.restaurantId,
                                restaurantEntity.restaurantName,
                                restaurantEntity.restaurantCuisine,
                                restaurantEntity.restaurantPosition,
                                restaurantEntity.restaurantImgUrl,
                                ratingEntity.tier,
                                ratingEntity.isTemp,
                                restaurantEntity.longitude,
                                restaurantEntity.latitude,
                                restaurantEntity.partnershipInfo,
                                ratingEntity.finalScore,
                                set(situationEntity.situationName),
                                restaurantEntity.restaurantType
                        )
                ));

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
    }

    // -- v2 추가

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Observed
    public List<RestaurantBaseInfoDtoV2> getRestaurantTiersBaseV2(List<Long> ids) {
        Map<Long, RestaurantBaseInfoDtoV2> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(restaurantCommonExpressions.situationMatches(restaurantSituationRelationEntity, restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(restaurantPartnershipEntity)
                .on(restaurantPartnershipEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(restaurantEntity.restaurantId.in(ids))
                .transform(groupBy(restaurantEntity.restaurantId).as(
                        new QRestaurantBaseInfoDtoV2(
                                restaurantEntity.restaurantId,
                                restaurantEntity.restaurantName,
                                restaurantEntity.restaurantCuisine,
                                restaurantEntity.restaurantPosition,
                                restaurantEntity.restaurantImgUrl,
                                ratingEntity.tier,
                                ratingEntity.isTemp,
                                restaurantEntity.longitude,
                                restaurantEntity.latitude,
                                set(partnershipText()),
                                ratingEntity.finalScore,
                                set(situationEntity.situationName),
                                restaurantEntity.restaurantType
                        )
                ));

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
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

    // -- v2 추가 끝

    @Observed
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Set<Long> findUserEvaluatedIds(Long userId, List<Long> restaurantIds) {
        return new HashSet<>(queryFactory
                .select(evaluationEntity.restaurantId)
                .from(evaluationEntity)
                .where(evaluationEntity.userId.eq(userId),
                        evaluationEntity.restaurantId.in(restaurantIds))
                .fetch());
    }

    @Observed
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Set<Long> findUserFavoriteIds(Long userId, List<Long> restaurantIds) {
        return new HashSet<>(queryFactory
                .select(restaurantFavoriteEntity.restaurantId)
                .from(restaurantFavoriteEntity)
                .where(restaurantFavoriteEntity.userId.eq(userId),
                        restaurantFavoriteEntity.restaurantId.in(restaurantIds))
                .fetch());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<RestaurantCoreInfoDto> getRestaurantTiers(List<Long> restaurantIds, Long userId) {
        Map<Long, RestaurantCoreInfoDto> map = queryFactory
                .from(restaurantEntity)
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .leftJoin(restaurantSituationRelationEntity)
                .on(restaurantCommonExpressions.situationMatches(restaurantSituationRelationEntity,
                        restaurantEntity.restaurantId))
                .leftJoin(situationEntity)
                .on(situationIdEq(restaurantSituationRelationEntity.situationId))
                .leftJoin(evaluationEntity)
                .on(evaluationRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .leftJoin(restaurantFavoriteEntity)
                .on(favoriteRestaurantIdEq(restaurantEntity.restaurantId, userId))
                .where(restaurantEntity.restaurantId.in(restaurantIds))
                .transform(
                        groupBy(restaurantEntity.restaurantId).as(
                                new QRestaurantCoreInfoDto(
                                        restaurantEntity.restaurantId,
                                        restaurantEntity.restaurantName,
                                        restaurantEntity.restaurantCuisine,
                                        restaurantEntity.restaurantPosition,
                                        restaurantEntity.restaurantImgUrl,
                                        ratingEntity.tier,
                                        ratingEntity.isTemp,
                                        restaurantEntity.longitude,
                                        restaurantEntity.latitude,
                                        restaurantEntity.partnershipInfo,
                                        numberTemplate(Double.class, "ROUND({0}, 2)", ratingEntity.finalScore.coalesce(0.0)),
                                        set(situationEntity.situationName),
                                        restaurantEntity.restaurantType,
                                        evaluationEntity.isNotNull(),
                                        restaurantFavoriteEntity.isNotNull()
                                )
                        )
                );

        return restaurantIds.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .toList();
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
}
