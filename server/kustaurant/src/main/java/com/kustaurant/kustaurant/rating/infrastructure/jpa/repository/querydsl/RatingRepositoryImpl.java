package com.kustaurant.kustaurant.rating.infrastructure.jpa.repository.querydsl;

import static com.kustaurant.jpa.rating.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
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
//        List<Long> ids = rating.stream().map(Rating::getRestaurantId).distinct().toList();
//        List<RatingEntity> existed = ratingJpaRepository.findAllByIdInForUpdate(ids);
//        Map<Long, RatingEntity> existedMap = new HashMap<>();
//        for (RatingEntity ratingEntity : existed) {
//            existedMap.put(ratingEntity.getRestaurantId(), ratingEntity);
//        }
//
//        List<RatingEntity> created = new ArrayList<>();
//
//        for (Rating r : rating) {
//            if (existedMap.containsKey(r.getRestaurantId())) {
//                existedMap.get(r.getRestaurantId()).updateRatingData(
//                        r.getScore(), r.getAiScore(), r.getTier().getValue(), r.isTemp(), r.getRatedAt(), r.getFinalScore()
//                );
//            } else {
//                created.add(RatingMapper.from(r));
//            }
//        }
//
//        ratingJpaRepository.saveAll(created);
    }
}
