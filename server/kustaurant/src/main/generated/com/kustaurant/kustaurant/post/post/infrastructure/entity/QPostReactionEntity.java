package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostReactionEntity is a Querydsl query type for PostReactionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostReactionEntity extends EntityPathBase<PostReactionEntity> {

    private static final long serialVersionUID = 1383042553L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostReactionEntity postReactionEntity = new QPostReactionEntity("postReactionEntity");

    public final QPostReactionJpaId id;

    public final DateTimePath<java.time.LocalDateTime> reactedAt = createDateTime("reactedAt", java.time.LocalDateTime.class);

    public final EnumPath<com.kustaurant.kustaurant.common.enums.ReactionType> reaction = createEnum("reaction", com.kustaurant.kustaurant.common.enums.ReactionType.class);

    public QPostReactionEntity(String variable) {
        this(PostReactionEntity.class, forVariable(variable), INITS);
    }

    public QPostReactionEntity(Path<? extends PostReactionEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostReactionEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostReactionEntity(PathMetadata metadata, PathInits inits) {
        this(PostReactionEntity.class, metadata, inits);
    }

    public QPostReactionEntity(Class<? extends PostReactionEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QPostReactionJpaId(forProperty("id")) : null;
    }

}

