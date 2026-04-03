package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostEntity is a Querydsl query type for PostEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostEntity extends EntityPathBase<PostEntity> {

    private static final long serialVersionUID = -911366288L;

    public static final QPostEntity postEntity = new QPostEntity("postEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> netLikes = createNumber("netLikes", Integer.class);

    public final StringPath postBody = createString("postBody");

    public final EnumPath<com.kustaurant.kustaurant.post.post.domain.enums.PostCategory> postCategory = createEnum("postCategory", com.kustaurant.kustaurant.post.post.domain.enums.PostCategory.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath postTitle = createString("postTitle");

    public final NumberPath<Integer> postVisitCount = createNumber("postVisitCount", Integer.class);

    public final EnumPath<com.kustaurant.kustaurant.post.post.domain.enums.PostStatus> status = createEnum("status", com.kustaurant.kustaurant.post.post.domain.enums.PostStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostEntity(String variable) {
        super(PostEntity.class, forVariable(variable));
    }

    public QPostEntity(Path<? extends PostEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostEntity(PathMetadata metadata) {
        super(PostEntity.class, metadata);
    }

}

