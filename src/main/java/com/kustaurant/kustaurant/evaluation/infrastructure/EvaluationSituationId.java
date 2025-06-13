package com.kustaurant.kustaurant.evaluation.infrastructure;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
public class EvaluationSituationId implements Serializable {
    private Integer evaluation;
    private Integer situation;

    // Default constructor
    public EvaluationSituationId() {
    }

    // Constructor with parameters
    public EvaluationSituationId(Integer evaluation, Integer situation) {
        this.evaluation = evaluation;
        this.situation = situation;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationSituationId)) return false;
        EvaluationSituationId that = (EvaluationSituationId) o;
        return Objects.equals(getEvaluation(), that.getEvaluation()) &&
                Objects.equals(getSituation(), that.getSituation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvaluation(), getSituation());
    }

}
