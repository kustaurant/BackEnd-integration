package com.kustaurant.kustaurant.admin.modal;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHomeModalEntity is a Querydsl query type for HomeModalEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHomeModalEntity extends EntityPathBase<HomeModalEntity> {

    private static final long serialVersionUID = 861760740L;

    public static final QHomeModalEntity homeModalEntity = new QHomeModalEntity("homeModalEntity");

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath title = createString("title");

    public QHomeModalEntity(String variable) {
        super(HomeModalEntity.class, forVariable(variable));
    }

    public QHomeModalEntity(Path<? extends HomeModalEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHomeModalEntity(PathMetadata metadata) {
        super(HomeModalEntity.class, metadata);
    }

}

