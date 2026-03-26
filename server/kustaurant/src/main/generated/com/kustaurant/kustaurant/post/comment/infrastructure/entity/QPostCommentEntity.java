package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostCommentEntity is a Querydsl query type for PostCommentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostCommentEntity extends EntityPathBase<PostCommentEntity> {

    private static final long serialVersionUID = -356539566L;

    public static final QPostCommentEntity postCommentEntity = new QPostCommentEntity("postCommentEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    public final StringPath commentBody = createString("commentBody");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> parentCommentId = createNumber("parentCommentId", Long.class);

    public final NumberPath<Long> postCommentId = createNumber("postCommentId", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus> status = createEnum("status", com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostCommentEntity(String variable) {
        super(PostCommentEntity.class, forVariable(variable));
    }

    public QPostCommentEntity(Path<? extends PostCommentEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostCommentEntity(PathMetadata metadata) {
        super(PostCommentEntity.class, metadata);
    }

}

