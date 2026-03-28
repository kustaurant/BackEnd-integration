package com.kustaurant.jpa.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantFavoriteEntity is a Querydsl query type for RestaurantFavoriteEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantFavoriteEntity extends EntityPathBase<RestaurantFavoriteEntity> {

    private static final long serialVersionUID = 1633727958L;

    public static final QRestaurantFavoriteEntity restaurantFavoriteEntity = new QRestaurantFavoriteEntity("restaurantFavoriteEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final StringPath status = createString("status");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRestaurantFavoriteEntity(String variable) {
        super(RestaurantFavoriteEntity.class, forVariable(variable));
    }

    public QRestaurantFavoriteEntity(Path<? extends RestaurantFavoriteEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantFavoriteEntity(PathMetadata metadata) {
        super(RestaurantFavoriteEntity.class, metadata);
    }

}

