package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class EvaluationSituationId implements Serializable {
    private Long evaluationId;
    private Long situationId;

    public EvaluationSituationId() {
    }

    public EvaluationSituationId(Long evaluationId, Long situationId) {
        this.evaluationId = evaluationId;
        this.situationId = situationId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationSituationId)) return false;
        EvaluationSituationId that = (EvaluationSituationId) o;
        return Objects.equals(getEvaluationId(), that.getEvaluationId()) &&
                Objects.equals(getSituationId(), that.getSituationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvaluationId(), getSituationId());
    }
}
