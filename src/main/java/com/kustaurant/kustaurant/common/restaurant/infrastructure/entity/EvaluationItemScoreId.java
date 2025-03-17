package com.kustaurant.kustaurant.common.restaurant.infrastructure.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
public class EvaluationItemScoreId implements Serializable {
    private Integer evaluation;
    private Integer situation;

    // Default constructor
    public EvaluationItemScoreId() {
    }

    // Constructor with parameters
    public EvaluationItemScoreId(Integer evaluation, Integer situation) {
        this.evaluation = evaluation;
        this.situation = situation;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationItemScoreId)) return false;
        EvaluationItemScoreId that = (EvaluationItemScoreId) o;
        return Objects.equals(getEvaluation(), that.getEvaluation()) &&
                Objects.equals(getSituation(), that.getSituation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvaluation(), getSituation());
    }

}
