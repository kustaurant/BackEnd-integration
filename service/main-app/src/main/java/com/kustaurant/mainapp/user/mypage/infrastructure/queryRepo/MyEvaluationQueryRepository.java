package com.kustaurant.mainapp.user.mypage.infrastructure.queryRepo;

import com.kustaurant.jpa.restaurant.entity.QRestaurantEntity;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.QEvaluationEntity;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.QSituationEntity;
import com.kustaurant.mainapp.user.mypage.controller.response.api.MyRatedRestaurantResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyEvaluationQueryRepository{
    private final JPAQueryFactory factory;

    private static final QEvaluationEntity evaluation = QEvaluationEntity.evaluationEntity;
    private static final QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;
    private static final QSituationEntity situation = QSituationEntity.situationEntity;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MyRatedRestaurantResponse> findByUserId(Long userId) {

        // -------- 1. 메인 정보 (Evaluation + Restaurant) --------
        List<Tuple> rows = factory.select(
                        evaluation.id,
                        restaurant.restaurantId,
                        restaurant.restaurantName,
                        restaurant.restaurantImgUrl,
                        restaurant.restaurantCuisine,
                        evaluation.evaluationScore,
                        evaluation.body
                )
                .from(evaluation)
                .join(restaurant).on(restaurant.restaurantId.eq(evaluation.restaurantId))
                .where(
                        evaluation.userId.eq(userId),
                        evaluation.status.eq("ACTIVE")
                )
                .orderBy(evaluation.createdAt.desc())
                .fetch();

        List<Long> evalIds = rows.stream()
                .map(t -> t.get(evaluation.id))
                .toList();

        if (evalIds.isEmpty()) return List.of();

        // -------- 2. 상황 이름 매핑 --------
        NumberPath<Long> sid = Expressions.numberPath(Long.class, "sid");

        Map<Long, List<String>> evalIdToSituations =
                factory.select(evaluation.id, situation.situationName)
                        .from(evaluation)
                        .join(evaluation.situationIds, sid)
                        .join(situation).on(situation.situationId.eq(sid))
                        .where(evaluation.id.in(evalIds))
                        .fetch()
                        .stream()
                        .collect(Collectors.groupingBy(
                                t -> t.get(evaluation.id),
                                Collectors.mapping(t -> t.get(situation.situationName), Collectors.toList())
                        ));

        /* -------- 3. DTO 변환 -------- */
        return rows.stream()
                .map(t -> new MyRatedRestaurantResponse(
                        t.get(restaurant.restaurantId),
                        t.get(restaurant.restaurantName),
                        t.get(restaurant.restaurantImgUrl),
                        t.get(restaurant.restaurantCuisine),
                        t.get(evaluation.evaluationScore),
                        t.get(evaluation.body),
                        evalIdToSituations.getOrDefault(t.get(evaluation.id), List.of())
                ))
                .toList();
    }
}
