package com.kustaurant.kustaurant.post.comment.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostCommentReactionJpaId is a Querydsl query type for PostCommentReactionJpaId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPostCommentReactionJpaId extends BeanPath<PostCommentReactionJpaId> {

    private static final long serialVersionUID = 948377438L;

    public static final QPostCommentReactionJpaId postCommentReactionJpaId = new QPostCommentReactionJpaId("postCommentReactionJpaId");

    public final NumberPath<Long> postCommentId = createNumber("postCommentId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QPostCommentReactionJpaId(String variable) {
        super(PostCommentReactionJpaId.class, forVariable(variable));
    }

    public QPostCommentReactionJpaId(Path<? extends PostCommentReactionJpaId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostCommentReactionJpaId(PathMetadata metadata) {
        super(PostCommentReactionJpaId.class, metadata);
    }

}

