package com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEvalCommentEntity is a Querydsl query type for EvalCommentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvalCommentEntity extends EntityPathBase<EvalCommentEntity> {

    private static final long serialVersionUID = -560988750L;

    public static final QEvalCommentEntity evalCommentEntity = new QEvalCommentEntity("evalCommentEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    public final StringPath body = createString("body");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> dislikeCount = createNumber("dislikeCount", Integer.class);

    public final NumberPath<Long> evaluationId = createNumber("evaluationId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.common.enums.Status> status = createEnum("status", com.kustaurant.kustaurant.common.enums.Status.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QEvalCommentEntity(String variable) {
        super(EvalCommentEntity.class, forVariable(variable));
    }

    public QEvalCommentEntity(Path<? extends EvalCommentEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvalCommentEntity(PathMetadata metadata) {
        super(EvalCommentEntity.class, metadata);
    }

}

