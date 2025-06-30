package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.SituationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@IdClass(EvaluationSituationId.class)
@Table(name = "evaluation_situations_tbl")
public class EvaluationSituationEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="evaluation_id")
    private EvaluationEntity evaluation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="situation_id")
    private SituationEntity situation;

    public EvaluationSituationEntity(EvaluationEntity evaluation, SituationEntity situation) {
        this.evaluation = evaluation;
        this.situation = situation;
    }


    public EvaluationSituationEntity() {

    }
}
