package com.kustaurant.mainapp.restaurant.query.common.infrastructure.repository;

import static com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.*;
import static com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.QRestaurantMenuEntity.restaurantMenuEntity;

import com.kustaurant.mainapp.restaurant.query.search.RestaurantSearchRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RestaurantSearchRepositoryImpl implements RestaurantSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Long> searchRestaurantIds(String[] kwArr, int size) {
        BooleanBuilder andBuilder = new BooleanBuilder();
        for (String kw : kwArr) {
            String pattern = "%" + kw + "%";

            BooleanBuilder orBuilder = new BooleanBuilder()
                    .or(restaurantEntity.restaurantCuisine.like(pattern))
                    .or(restaurantEntity.restaurantName.like(pattern))
                    .or(restaurantEntity.restaurantType.like(pattern));

            BooleanExpression menuExists = JPAExpressions
                    .selectOne()
                    .from(restaurantMenuEntity)
                    .where(
                            restaurantMenuEntity.restaurantId.eq(restaurantEntity.restaurantId),
                            restaurantMenuEntity.menuName.like(pattern)
                    )
                    .exists();

            andBuilder.and(orBuilder.or(menuExists));
        }

        return queryFactory
                .select(restaurantEntity.restaurantId)
                .from(restaurantEntity)
                .where(andBuilder)
                .limit(size)
                .fetch();
    }
}
