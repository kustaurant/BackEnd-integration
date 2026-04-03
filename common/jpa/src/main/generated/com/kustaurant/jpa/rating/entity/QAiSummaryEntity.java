package com.kustaurant.jpa.rating.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAiSummaryEntity is a Querydsl query type for AiSummaryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAiSummaryEntity extends EntityPathBase<AiSummaryEntity> {

    private static final long serialVersionUID = 1393616807L;

    public static final QAiSummaryEntity aiSummaryEntity = new QAiSummaryEntity("aiSummaryEntity");

    public final NumberPath<Double> avgScore = createNumber("avgScore", Double.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> lastAnalyzedAt = createDateTime("lastAnalyzedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> lastJobId = createNumber("lastJobId", Long.class);

    public final NumberPath<Integer> negativeReviewCount = createNumber("negativeReviewCount", Integer.class);

    public final NumberPath<Integer> positiveReviewCount = createNumber("positiveReviewCount", Integer.class);

    public final NumberPath<Long> restaurantId = createNumber("restaurantId", Long.class);

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final NumberPath<Double> totalScoreSum = createNumber("totalScoreSum", Double.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAiSummaryEntity(String variable) {
        super(AiSummaryEntity.class, forVariable(variable));
    }

    public QAiSummaryEntity(Path<? extends AiSummaryEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAiSummaryEntity(PathMetadata metadata) {
        super(AiSummaryEntity.class, metadata);
    }

}

