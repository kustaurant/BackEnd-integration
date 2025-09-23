package com.kustaurant.mainapp.evaluation.evaluation.service;

import com.kustaurant.mainapp.evaluation.evaluation.domain.RestaurantSituationRelation;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.RestaurantSituationRelationRepository;
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

    @Transactional
    public void afterEvaluationCreated(Long restaurantId, List<Long> situationIds) {
        // 식당 상황 수 테이블 업데이트
        increaseSituationRelationCounts(restaurantId, situationIds);
    }

    @Transactional
    public void afterReEvaluated(
            Long restaurantId,
            List<Long> preSituations,
            List<Long> postSituations
    ) {
        // 식당 상황 수 테이블 업데이트
        syncSituationRelationCountsForRemoved(restaurantId, preSituations, postSituations);
    }

    private void increaseSituationRelationCounts(Long restaurantId, List<Long> situationIds) {
        for (Long situationId : situationIds) {
            updateOrCreateRelation(restaurantId, situationId, 1);
        }
    }

    private void syncSituationRelationCountsForRemoved(Long restaurantId,
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
    private void updateOrCreateRelation(Long restaurantId, Long situationId, Integer addDataCount) {
        relationRepository.findByRestaurantIdAndSituationId(restaurantId, situationId)
                .ifPresentOrElse(
                        r -> updateRelation(r, addDataCount), // 기존에 있는 경우 업데이트
                        () -> saveRelationWhenAddingPositive(restaurantId, situationId, addDataCount) // 없는 경우 추가
                );
    }

    private void updateRelation(RestaurantSituationRelation relation, Integer addDataCount) {
        relation.addDataCount(addDataCount);
        relationRepository.changeDataCount(relation);
    }

    private void saveRelationWhenAddingPositive(Long restaurantId, Long situationId, Integer addDataCount) {
        if (addDataCount > 0) {
            relationRepository.create(RestaurantSituationRelation.create(situationId, restaurantId, addDataCount));
        }
    }
}
