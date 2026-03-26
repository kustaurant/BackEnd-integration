package com.kustaurant.jpa.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantEntity is a Querydsl query type for RestaurantEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantEntity extends EntityPathBase<RestaurantEntity> {

    private static final long serialVersionUID = -201536486L;

    public static final QRestaurantEntity restaurantEntity = new QRestaurantEntity("restaurantEntity");

    public final com.kustaurant.jpa.common.entity.QBaseTimeEntity _super = new com.kustaurant.jpa.common.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath partnershipInfo = createString("partnershipInfo");

    public final StringPath restaurantAddress = createString("restaurantAddress");

    public final StringPath restaurantCuisine = createString("restaurantCuisine");

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final StringPath restaurantImgUrl = createString("restaurantImgUrl");

    public final StringPath restaurantName = createString("restaurantName");

    public final StringPath restaurantPosition = createString("restaurantPosition");

    public final StringPath restaurantTel = createString("restaurantTel");

    public final StringPath restaurantType = createString("restaurantType");

    public final StringPath restaurantUrl = createString("restaurantUrl");

    public final StringPath status = createString("status");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> visitCount = createNumber("visitCount", Integer.class);

    public QRestaurantEntity(String variable) {
        super(RestaurantEntity.class, forVariable(variable));
    }

    public QRestaurantEntity(Path<? extends RestaurantEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantEntity(PathMetadata metadata) {
        super(RestaurantEntity.class, metadata);
    }

}

