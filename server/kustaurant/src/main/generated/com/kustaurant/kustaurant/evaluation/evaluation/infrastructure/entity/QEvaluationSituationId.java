package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEvaluationSituationId is a Querydsl query type for EvaluationSituationId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QEvaluationSituationId extends BeanPath<EvaluationSituationId> {

    private static final long serialVersionUID = 1228723568L;

    public static final QEvaluationSituationId evaluationSituationId = new QEvaluationSituationId("evaluationSituationId");

    public final NumberPath<Long> evaluationId = createNumber("evaluationId", Long.class);

    public final NumberPath<Long> situationId = createNumber("situationId", Long.class);

    public QEvaluationSituationId(String variable) {
        super(EvaluationSituationId.class, forVariable(variable));
    }

    public QEvaluationSituationId(Path<? extends EvaluationSituationId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvaluationSituationId(PathMetadata metadata) {
        super(EvaluationSituationId.class, metadata);
    }

}

