package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEvaluationReactionEntity is a Querydsl query type for EvaluationReactionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvaluationReactionEntity extends EntityPathBase<EvaluationReactionEntity> {

    private static final long serialVersionUID = -786743723L;

    public static final QEvaluationReactionEntity evaluationReactionEntity = new QEvaluationReactionEntity("evaluationReactionEntity");

    public final NumberPath<Long> evaluationId = createNumber("evaluationId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.common.enums.ReactionType> reaction = createEnum("reaction", com.kustaurant.kustaurant.common.enums.ReactionType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QEvaluationReactionEntity(String variable) {
        super(EvaluationReactionEntity.class, forVariable(variable));
    }

    public QEvaluationReactionEntity(Path<? extends EvaluationReactionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvaluationReactionEntity(PathMetadata metadata) {
        super(EvaluationReactionEntity.class, metadata);
    }

}

