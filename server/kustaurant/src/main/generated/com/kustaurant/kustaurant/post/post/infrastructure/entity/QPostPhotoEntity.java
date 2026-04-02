package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPostPhotoEntity is a Querydsl query type for PostPhotoEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostPhotoEntity extends EntityPathBase<PostPhotoEntity> {

    private static final long serialVersionUID = -715354808L;

    public static final QPostPhotoEntity postPhotoEntity = new QPostPhotoEntity("postPhotoEntity");

    public final NumberPath<Integer> photoId = createNumber("photoId", Integer.class);

    public final StringPath photoImgUrl = createString("photoImgUrl");

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.post.post.domain.enums.PostStatus> status = createEnum("status", com.kustaurant.kustaurant.post.post.domain.enums.PostStatus.class);

    public QPostPhotoEntity(String variable) {
        super(PostPhotoEntity.class, forVariable(variable));
    }

    public QPostPhotoEntity(Path<? extends PostPhotoEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostPhotoEntity(PathMetadata metadata) {
        super(PostPhotoEntity.class, metadata);
    }

}

