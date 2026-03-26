package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvaluationSituationEntity is a Querydsl query type for EvaluationSituationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvaluationSituationEntity extends EntityPathBase<EvaluationSituationEntity> {

    private static final long serialVersionUID = 82082232L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEvaluationSituationEntity evaluationSituationEntity = new QEvaluationSituationEntity("evaluationSituationEntity");

    public final QEvaluationSituationId id;

    public QEvaluationSituationEntity(String variable) {
        this(EvaluationSituationEntity.class, forVariable(variable), INITS);
    }

    public QEvaluationSituationEntity(Path<? extends EvaluationSituationEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEvaluationSituationEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEvaluationSituationEntity(PathMetadata metadata, PathInits inits) {
        this(EvaluationSituationEntity.class, metadata, inits);
    }

    public QEvaluationSituationEntity(Class<? extends EvaluationSituationEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QEvaluationSituationId(forProperty("id")) : null;
    }

}

