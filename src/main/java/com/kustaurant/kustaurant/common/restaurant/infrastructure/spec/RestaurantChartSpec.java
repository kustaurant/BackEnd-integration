package com.kustaurant.kustaurant.common.restaurant.infrastructure.spec;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.situation.RestaurantSituationRelationEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantChartSpec {
    // TODO:
    // TODO: 상황 기준 작성 해야됨.
    public static boolean hasSituation(RestaurantSituationRelationEntity restaurantSituationRelationEntity) {
        return restaurantSituationRelationEntity.getDataCount() > 3;
    }
    // TODO: 상황 기준 작성 해야됨.
    public static Specification<RestaurantEntity> withCuisinesAndLocationsAndSituations(
            List<String> cuisines,
            List<String> locations,
            List<Integer> situationList,
            String status,
            Integer tierInfo,
            boolean isOrderByScore
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            // null인 경우 전체임
            if (cuisines != null) {
                if (cuisines.contains("JH")) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get("partnershipInfo")),
                            criteriaBuilder.notEqual(root.get("partnershipInfo"), "")
                    ));
                } else {
                    predicates.add(root.get("restaurantCuisine").in(cuisines));
                }
            }

            // null인 경우 전체임
            if (locations != null) {
                predicates.add(root.get("restaurantPosition").in(locations));
            }

            // null인 경우 전체임
            if (situationList != null && !situationList.isEmpty()) {
                // 서브쿼리 생성
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<RestaurantSituationRelationEntity> subRoot = subquery.from(RestaurantSituationRelationEntity.class);

                // 서브쿼리 조건: situationId가 situationList에 속하고, dataCount가 3보다 큰 항목
                Predicate situationPredicate = subRoot.get("situation").get("situationId").in(situationList);
                Predicate dataCountPredicate = criteriaBuilder.gt(subRoot.get("dataCount"), 3);

                // Restaurant과 RestaurantSituationRelation 간의 조인
                Predicate joinPredicate = criteriaBuilder.equal(subRoot.get("restaurant"), root);

                subquery.select(criteriaBuilder.count(subRoot))
                        .where(criteriaBuilder.and(situationPredicate, dataCountPredicate, joinPredicate));

                // 서브쿼리가 0보다 큰지 확인하는 조건 추가
                predicates.add(criteriaBuilder.greaterThan(subquery, 0L));
            }

            // 1인 경우 티어가 있는 식당만. -1인 경우 티어가 없는 식당만.
            if (tierInfo != null) {
                if (tierInfo == 1) {
                    predicates.add(criteriaBuilder.gt(root.get("mainTier"), 0));
                }
                if (tierInfo == -1) {
                    predicates.add(criteriaBuilder.equal(root.get("mainTier"), -1));
                }
            }

            if (isOrderByScore) {
                query.orderBy(
                        criteriaBuilder.desc(
                                criteriaBuilder.selectCase()
                                        .when(criteriaBuilder.lt(root.get("mainTier"), 0), 0)
                                        .otherwise(criteriaBuilder.quot(root.get("restaurantScoreSum"), root.get("restaurantEvaluationCount"))))
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
