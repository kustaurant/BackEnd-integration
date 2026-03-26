package com.kustaurant.jpa.restaurant.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestaurantMenuEntity is a Querydsl query type for RestaurantMenuEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRestaurantMenuEntity extends EntityPathBase<RestaurantMenuEntity> {

    private static final long serialVersionUID = -118221095L;

    public static final QRestaurantMenuEntity restaurantMenuEntity = new QRestaurantMenuEntity("restaurantMenuEntity");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath menuImgUrl = createString("menuImgUrl");

    public final StringPath menuName = createString("menuName");

    public final StringPath menuPrice = createString("menuPrice");

    public final StringPath naverType = createString("naverType");

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public QRestaurantMenuEntity(String variable) {
        super(RestaurantMenuEntity.class, forVariable(variable));
    }

    public QRestaurantMenuEntity(Path<? extends RestaurantMenuEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestaurantMenuEntity(PathMetadata metadata) {
        super(RestaurantMenuEntity.class, metadata);
    }

}

