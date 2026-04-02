package com.kustaurant.kustaurant.user.mypage.infrastructure;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserStatsEntity is a Querydsl query type for UserStatsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserStatsEntity extends EntityPathBase<UserStatsEntity> {

    private static final long serialVersionUID = 1960353513L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserStatsEntity userStatsEntity = new QUserStatsEntity("userStatsEntity");

    public final NumberPath<Integer> commCommentCnt = createNumber("commCommentCnt", Integer.class);

    public final NumberPath<Integer> commPostCnt = createNumber("commPostCnt", Integer.class);

    public final NumberPath<Integer> commSavedPostCnt = createNumber("commSavedPostCnt", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> ratedRestCnt = createNumber("ratedRestCnt", Integer.class);

    public final NumberPath<Integer> savedRestCnt = createNumber("savedRestCnt", Integer.class);

    public final com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity user;

    public QUserStatsEntity(String variable) {
        this(UserStatsEntity.class, forVariable(variable), INITS);
    }

    public QUserStatsEntity(Path<? extends UserStatsEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserStatsEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserStatsEntity(PathMetadata metadata, PathInits inits) {
        this(UserStatsEntity.class, metadata, inits);
    }

    public QUserStatsEntity(Class<? extends UserStatsEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

