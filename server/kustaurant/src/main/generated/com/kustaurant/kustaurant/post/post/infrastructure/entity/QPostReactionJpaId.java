package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostReactionJpaId is a Querydsl query type for PostReactionJpaId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostReactionJpaId extends BeanPath<PostReactionJpaId> {

    private static final long serialVersionUID = 49272192L;

    public static final QPostReactionJpaId postReactionJpaId = new QPostReactionJpaId("postReactionJpaId");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostReactionJpaId(String variable) {
        super(PostReactionJpaId.class, forVariable(variable));
    }

    public QPostReactionJpaId(Path<? extends PostReactionJpaId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostReactionJpaId(PathMetadata metadata) {
        super(PostReactionJpaId.class, metadata);
    }

}

