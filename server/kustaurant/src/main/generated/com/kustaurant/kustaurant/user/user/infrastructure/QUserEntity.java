package com.kustaurant.kustaurant.user.user.infrastructure;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -660195060L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.user.login.api.domain.LoginApi> loginApi = createEnum("loginApi", com.kustaurant.kustaurant.user.login.api.domain.LoginApi.class);

    public final com.kustaurant.kustaurant.user.user.domain.QNickname nickname;

    public final com.kustaurant.kustaurant.user.user.domain.QPhoneNumber phoneNumber;

    public final StringPath providerId = createString("providerId");

    public final EnumPath<com.kustaurant.kustaurant.user.user.domain.UserRole> role = createEnum("role", com.kustaurant.kustaurant.user.user.domain.UserRole.class);

    public final com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity stats;

    public final EnumPath<com.kustaurant.kustaurant.user.user.domain.UserStatus> status = createEnum("status", com.kustaurant.kustaurant.user.user.domain.UserStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QUserEntity(String variable) {
        this(UserEntity.class, forVariable(variable), INITS);
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserEntity(PathMetadata metadata, PathInits inits) {
        this(UserEntity.class, metadata, inits);
    }

    public QUserEntity(Class<? extends UserEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.nickname = inits.isInitialized("nickname") ? new com.kustaurant.kustaurant.user.user.domain.QNickname(forProperty("nickname")) : null;
        this.phoneNumber = inits.isInitialized("phoneNumber") ? new com.kustaurant.kustaurant.user.user.domain.QPhoneNumber(forProperty("phoneNumber")) : null;
        this.stats = inits.isInitialized("stats") ? new com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity(forProperty("stats"), inits.get("stats")) : null;
    }

}

