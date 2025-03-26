package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.situation.SituationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@IdClass(EvaluationItemScoreId.class)
@Table(name="evaluation_item_scores_tbl")
public class EvaluationItemScore {

    @Id
    @ManyToOne
    @JoinColumn(name="evaluation_id")
    private EvaluationEntity evaluation;

    @Id
    @ManyToOne
    @JoinColumn(name="situation_id")
    private SituationEntity situation;

    public EvaluationItemScore(EvaluationEntity evaluation, SituationEntity situation) {
        this.evaluation = evaluation;
        this.situation = situation;
    }


    public EvaluationItemScore() {

    }
}
