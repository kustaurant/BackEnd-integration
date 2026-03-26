package com.kustaurant.jpa.rating.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRatingEntity is a Querydsl query type for RatingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRatingEntity extends EntityPathBase<RatingEntity> {

    private static final long serialVersionUID = 165772762L;

    public static final QRatingEntity ratingEntity = new QRatingEntity("ratingEntity");

    public final NumberPath<Double> finalScore = createNumber("finalScore", Double.class);

    public final BooleanPath hasTier = createBoolean("hasTier");

    public final BooleanPath isTemp = createBoolean("isTemp");

    public final DateTimePath<java.time.LocalDateTime> ratedAt = createDateTime("ratedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final NumberPath<Double> selfScore = createNumber("selfScore", Double.class);

    public final NumberPath<Integer> tier = createNumber("tier", Integer.class);

    public QRatingEntity(String variable) {
        super(RatingEntity.class, forVariable(variable));
    }

    public QRatingEntity(Path<? extends RatingEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRatingEntity(PathMetadata metadata) {
        super(RatingEntity.class, metadata);
    }

}

