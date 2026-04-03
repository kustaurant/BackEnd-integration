package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvaluationEntity is a Querydsl query type for EvaluationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvaluationEntity extends EntityPathBase<EvaluationEntity> {

    private static final long serialVersionUID = -2041744436L;

    public static final QEvaluationEntity evaluationEntity = new QEvaluationEntity("evaluationEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    public final StringPath body = createString("body");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> dislikeCount = createNumber("dislikeCount", Integer.class);

    public final NumberPath<Double> evaluationScore = createNumber("evaluationScore", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final ListPath<Long, NumberPath<Long>> situationIds = this.<Long, NumberPath<Long>>createList("situationIds", Long.class, NumberPath.class, PathInits.DIRECT2);

    public final StringPath status = createString("status");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QEvaluationEntity(String variable) {
        super(EvaluationEntity.class, forVariable(variable));
    }

    public QEvaluationEntity(Path<? extends EvaluationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvaluationEntity(PathMetadata metadata) {
        super(EvaluationEntity.class, metadata);
    }

}

