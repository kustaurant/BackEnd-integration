package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSituationEntity is a Querydsl query type for SituationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSituationEntity extends EntityPathBase<SituationEntity> {

    private static final long serialVersionUID = -1486531500L;

    public static final QSituationEntity situationEntity = new QSituationEntity("situationEntity");

    public final NumberPath<Long> situationId = createNumber("situationId", Long.class);

    public final StringPath situationName = createString("situationName");

    public QSituationEntity(String variable) {
        super(SituationEntity.class, forVariable(variable));
    }

    public QSituationEntity(Path<? extends SituationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSituationEntity(PathMetadata metadata) {
        super(SituationEntity.class, metadata);
    }

}

