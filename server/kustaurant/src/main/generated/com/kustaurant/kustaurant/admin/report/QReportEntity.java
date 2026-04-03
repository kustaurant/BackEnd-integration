package com.kustaurant.kustaurant.admin.report;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportEntity is a Querydsl query type for ReportEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportEntity extends EntityPathBase<ReportEntity> {

    private static final long serialVersionUID = 1929791877L;

    public static final QReportEntity reportEntity = new QReportEntity("reportEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.admin.report.enums.ReportReason> reason = createEnum("reason", com.kustaurant.kustaurant.admin.report.enums.ReportReason.class);

    public final EnumPath<com.kustaurant.kustaurant.admin.report.enums.ReportStatus> status = createEnum("status", com.kustaurant.kustaurant.admin.report.enums.ReportStatus.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final EnumPath<com.kustaurant.kustaurant.admin.report.enums.TargetType> targetType = createEnum("targetType", com.kustaurant.kustaurant.admin.report.enums.TargetType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QReportEntity(String variable) {
        super(ReportEntity.class, forVariable(variable));
    }

    public QReportEntity(Path<? extends ReportEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportEntity(PathMetadata metadata) {
        super(ReportEntity.class, metadata);
    }

}

