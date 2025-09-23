package com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "evaluation_situation")
public class EvaluationSituationEntity {
    @EmbeddedId
    private EvaluationSituationId id;

    protected EvaluationSituationEntity() {}

    public EvaluationSituationEntity(Long evaluationId, Long situationId) {
        this.id = new EvaluationSituationId(evaluationId, situationId);
    }

    public Long getEvaluationId() { return id.getEvaluationId(); }
    public Long getSituationId() { return id.getSituationId(); }
}
