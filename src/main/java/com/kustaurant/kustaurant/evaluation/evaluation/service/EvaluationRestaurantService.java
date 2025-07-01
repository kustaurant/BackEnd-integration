package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.RestaurantSituationRelation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.RestaurantSituationRelationRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantRatingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 평가 관련 식당 데이터를 다루는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class EvaluationRestaurantService {

    private final RestaurantSituationRelationRepository relationRepository;

    private final RestaurantRatingService restaurantRatingService;

    @Transactional
    public void afterEvaluationCreated(Integer restaurantId, List<Long> situationIds, Double score) {
        // 식당 상황 수 테이블 업데이트
        increaseSituationRelationCounts(restaurantId, situationIds);
        // 식당 정보 업데이트
        restaurantRatingService.afterEvaluationCreated(restaurantId, score);
    }

    @Transactional
    public void afterReEvaluated(
            Integer restaurantId,
            List<Long> preSituations, List<Long> postSituations,
            Double preScore, Double postScore
    ) {
        // 식당 상황 수 테이블 업데이트
        syncSituationRelationCountsForRemoved(restaurantId, preSituations, postSituations);
        // 식당 정보 업데이트
        restaurantRatingService.afterReEvaluated(restaurantId, preScore, postScore);
    }

    private void increaseSituationRelationCounts(Integer restaurantId, List<Long> situationIds) {
        for (Long situationId : situationIds) {
            updateOrCreateRelation(restaurantId, situationId, 1);
        }
    }

    private void syncSituationRelationCountsForRemoved(Integer restaurantId,
            List<Long> preSituations, List<Long> postSituations) {
        for (Long preSituation : preSituations) { // 삭제된 situation 감소시키기
            if (postSituations.contains(preSituation)) {
                continue;
            }
            updateOrCreateRelation(restaurantId, preSituation, -1);
        }
        for (Long postSituation : postSituations) { // 추가된 situation 증가시키기
            if (preSituations.contains(postSituation)) {
                continue;
            }
            updateOrCreateRelation(restaurantId, postSituation, 1);
        }
    }

    // 기존에 데이터가 있으면 업데이트하고, 없으면 새로 생성합니다.
    private void updateOrCreateRelation(Integer restaurantId, Long situationId, Integer addDataCount) {
        relationRepository.findByRestaurantIdAndSituationId(restaurantId, situationId)
                .ifPresentOrElse(
                        r -> updateRelation(r, addDataCount), // 기존에 있는 경우 업데이트
                        () -> saveRelationWhenAddingPositive(restaurantId, situationId, addDataCount) // 없는 경우 추가
                );
    }

    private void updateRelation(RestaurantSituationRelation relation, Integer addDataCount) {
        relation.addDataCount(addDataCount);
        relationRepository.updateDataCount(relation);
    }

    private void saveRelationWhenAddingPositive(Integer restaurantId, Long situationId, Integer addDataCount) {
        if (addDataCount > 0) {
            relationRepository.create(RestaurantSituationRelation.create(situationId, restaurantId, addDataCount));
        }
    }
}
