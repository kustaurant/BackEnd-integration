package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantSituationRelationEntity is a Querydsl query type for RestaurantSituationRelationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantSituationRelationEntity extends EntityPathBase<RestaurantSituationRelationEntity> {

    private static final long serialVersionUID = 1205016755L;

    public static final QRestaurantSituationRelationEntity restaurantSituationRelationEntity = new QRestaurantSituationRelationEntity("restaurantSituationRelationEntity");

    public final NumberPath<Integer> dataCount = createNumber("dataCount", Integer.class);

    public final NumberPath<Long> relationId = createNumber("relationId", Long.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final NumberPath<Long> situationId = createNumber("situationId", Long.class);

    public QRestaurantSituationRelationEntity(String variable) {
        super(RestaurantSituationRelationEntity.class, forVariable(variable));
    }

    public QRestaurantSituationRelationEntity(Path<? extends RestaurantSituationRelationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantSituationRelationEntity(PathMetadata metadata) {
        super(RestaurantSituationRelationEntity.class, metadata);
    }

}

