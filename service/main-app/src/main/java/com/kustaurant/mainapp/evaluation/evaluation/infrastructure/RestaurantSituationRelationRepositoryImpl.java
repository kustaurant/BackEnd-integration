package com.kustaurant.mainapp.evaluation.evaluation.infrastructure;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.evaluation.evaluation.domain.RestaurantSituationRelation;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.RestaurantSituationRelationEntity;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa.RestaurantSituationRelationJpaRepository;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.RestaurantSituationRelationRepository;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
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
            Long restaurantId, Long situationId) {
        return jpaRepository.findByRestaurantIdAndSituationId(restaurantId, situationId)
                .map(RestaurantSituationRelationEntity::toModel);
    }

    @Override
    public Long create(RestaurantSituationRelation restaurantSituationRelation) {
        return jpaRepository.save(RestaurantSituationRelationEntity.create(restaurantSituationRelation)).getRelationId();
    }

    @Override
    public void changeDataCount(RestaurantSituationRelation relation) {
        RestaurantSituationRelationEntity entity = jpaRepository.findById(relation.getRelationId())
                .orElseThrow(() -> new DataNotFoundException(
                        RESTAURANT_SITUATION_RELATION_NOT_FOUND,
                        relation.getRelationId(),
                        "식당 상황 관계"
                ));

        entity.changeDataCount(relation.getDataCount());
    }
}
