package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostCommentReactionEntity is a Querydsl query type for PostCommentReactionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostCommentReactionEntity extends EntityPathBase<PostCommentReactionEntity> {

    private static final long serialVersionUID = -809465893L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostCommentReactionEntity postCommentReactionEntity = new QPostCommentReactionEntity("postCommentReactionEntity");

    public final QPostCommentReactionJpaId id;

    public final DateTimePath<java.time.LocalDateTime> reactedAt = createDateTime("reactedAt", java.time.LocalDateTime.class);

    public final EnumPath<com.kustaurant.kustaurant.common.enums.ReactionType> reaction = createEnum("reaction", com.kustaurant.kustaurant.common.enums.ReactionType.class);

    public QPostCommentReactionEntity(String variable) {
        this(PostCommentReactionEntity.class, forVariable(variable), INITS);
    }

    public QPostCommentReactionEntity(Path<? extends PostCommentReactionEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostCommentReactionEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostCommentReactionEntity(PathMetadata metadata, PathInits inits) {
        this(PostCommentReactionEntity.class, metadata, inits);
    }

    public QPostCommentReactionEntity(Class<? extends PostCommentReactionEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QPostCommentReactionJpaId(forProperty("id")) : null;
    }

}

