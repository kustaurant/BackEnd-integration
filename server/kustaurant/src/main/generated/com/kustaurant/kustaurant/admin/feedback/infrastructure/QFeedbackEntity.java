package com.kustaurant.kustaurant.admin.feedback.infrastructure;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFeedbackEntity is a Querydsl query type for FeedbackEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeedbackEntity extends EntityPathBase<FeedbackEntity> {

    private static final long serialVersionUID = 1023209910L;

    public static final QFeedbackEntity feedbackEntity = new QFeedbackEntity("feedbackEntity");

    public final StringPath comment = createString("comment");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QFeedbackEntity(String variable) {
        super(FeedbackEntity.class, forVariable(variable));
    }

    public QFeedbackEntity(Path<? extends FeedbackEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFeedbackEntity(PathMetadata metadata) {
        super(FeedbackEntity.class, metadata);
    }

}

