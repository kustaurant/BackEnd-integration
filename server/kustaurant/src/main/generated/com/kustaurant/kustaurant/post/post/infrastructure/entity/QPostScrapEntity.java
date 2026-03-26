package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostScrapEntity is a Querydsl query type for PostScrapEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostScrapEntity extends EntityPathBase<PostScrapEntity> {

    private static final long serialVersionUID = 177775527L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostScrapEntity postScrapEntity = new QPostScrapEntity("postScrapEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QPostReactionJpaId id;

    public QPostScrapEntity(String variable) {
        this(PostScrapEntity.class, forVariable(variable), INITS);
    }

    public QPostScrapEntity(Path<? extends PostScrapEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostScrapEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostScrapEntity(PathMetadata metadata, PathInits inits) {
        this(PostScrapEntity.class, metadata, inits);
    }

    public QPostScrapEntity(Class<? extends PostScrapEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QPostReactionJpaId(forProperty("id")) : null;
    }

}

