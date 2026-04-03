package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEvaluationCommentReactionEntity is a Querydsl query type for EvaluationCommentReactionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvaluationCommentReactionEntity extends EntityPathBase<EvaluationCommentReactionEntity> {

    private static final long serialVersionUID = -2034563429L;

    public static final QEvaluationCommentReactionEntity evaluationCommentReactionEntity = new QEvaluationCommentReactionEntity("evaluationCommentReactionEntity");

    public final NumberPath<Long> evalCommentId = createNumber("evalCommentId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.common.enums.ReactionType> reaction = createEnum("reaction", com.kustaurant.kustaurant.common.enums.ReactionType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QEvaluationCommentReactionEntity(String variable) {
        super(EvaluationCommentReactionEntity.class, forVariable(variable));
    }

    public QEvaluationCommentReactionEntity(Path<? extends EvaluationCommentReactionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvaluationCommentReactionEntity(PathMetadata metadata) {
        super(EvaluationCommentReactionEntity.class, metadata);
    }

}

