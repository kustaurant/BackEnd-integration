package com.kustaurant.restauranttier.tab3_tier.specification;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.etc.TierVariable;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantSpecification {
    public static Specification<Restaurant> withCuisinesAndLocations(List<String> cuisines, List<String> locations, String status, Integer tierInfo, boolean isOrderByScore) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("status"), status));

            // null인 경우 전체임
            if (cuisines != null) {
                if (cuisines.contains("JH")) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("partnershipInfo")));
                } else {
                    predicates.add(root.get("restaurantCuisine").in(cuisines));
                }
            }

            // null인 경우 전체임
            if (locations != null) {
                predicates.add(root.get("restaurantPosition").in(locations));
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
                                        .when(criteriaBuilder.lt(root.get("restaurantEvaluationCount"), TierVariable.minNumberOfEvaluations), 0)
                                        .otherwise(criteriaBuilder.quot(root.get("restaurantScoreSum"), root.get("restaurantEvaluationCount"))))
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
