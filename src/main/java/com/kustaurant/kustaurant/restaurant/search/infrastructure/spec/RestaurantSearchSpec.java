package com.kustaurant.kustaurant.restaurant.search.infrastructure.spec;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantMenuEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearchSpec {

    public static Specification<RestaurantEntity> createSearchSpecification(String[] kws) {
        return new Specification<RestaurantEntity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                // 조인
                Join<RestaurantEntity, RestaurantMenuEntity> joinMenu = root.join("restaurantMenuList", JoinType.LEFT);
                //Join<Restaurant, Situation> joinSituation = root.join("situationList", JoinType.LEFT);

                List<Predicate> predicates = new ArrayList<>();
                // 여기서 for문을 사용하여 각 항목에 대한 Predicate를 생성하고 predicates 리스트에 추가합니다.
                Predicate statusPredicate = cb.equal(root.get("status"), "ACTIVE");
                for (String kw : kws) {
                    Predicate namePredicate = cb.like(root.get("restaurantName"), "%" + kw + "%");
                    Predicate typePredicate = cb.like(root.get("restaurantType"), "%" + kw + "%");
                    Predicate cuisinePredicate = cb.like(root.get("restaurantCuisine"), "%" + kw + "%");
                    Predicate menuPredicate = cb.like(joinMenu.get("menuName"), "%" + kw + "%");

                    // 각 Predicate를 predicates 리스트에 추가합니다.
                    predicates.add(cb.and(
                            cb.equal(root.get("status"), "ACTIVE"),
                            cb.or(namePredicate, typePredicate, cuisinePredicate, menuPredicate)));
                }

                // predicates 리스트에 있는 모든 Predicate를 and() 메소드에 전달하여 모든 조건을 결합합니다.
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
