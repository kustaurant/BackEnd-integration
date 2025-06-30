package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.RestaurantSituationRelation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.RestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.RestaurantSituationRelationJpaRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.RestaurantSituationRelationRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantSituationRelationRepositoryImpl implements
        RestaurantSituationRelationRepository {

    private final RestaurantSituationRelationJpaRepository jpaRepository;

    @Override
    public Optional<RestaurantSituationRelation> findByRestaurantIdAndSituationId(
            Integer restaurantId, Long situationId) {
        return jpaRepository.findByRestaurantIdAndSituationId(restaurantId, situationId)
                .map(RestaurantSituationRelationEntity::toModel);
    }

    @Override
    public Long create(RestaurantSituationRelation restaurantSituationRelation) {
        return jpaRepository.save(RestaurantSituationRelationEntity.create(restaurantSituationRelation)).getRelationId();
    }

    @Override
    public void updateDataCount(RestaurantSituationRelation relation) {
        RestaurantSituationRelationEntity entity = jpaRepository.findById(relation.getRelationId())
                .orElseThrow(() -> new DataNotFoundException(
                        RESTAURANT_SITUATION_RELATION_NOT_FOUNT,
                        relation.getRelationId(),
                        "식당 상황 관계"
                ));

        entity.updateDataCount(relation.getDataCount());
    }
}
