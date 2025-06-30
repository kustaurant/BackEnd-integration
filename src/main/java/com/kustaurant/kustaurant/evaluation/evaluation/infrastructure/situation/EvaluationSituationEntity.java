package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationSituation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@IdClass(EvaluationSituationId.class)
@Table(name = "evaluation_situations_tbl")
@NoArgsConstructor
public class EvaluationSituationEntity {

    @Id
    @Column(name = "evaluation_id")
    private Long evaluationId;

    @Id
    @Column(name = "situation_id")
    private Long situationId;

    public EvaluationSituationEntity(Long evaluationId, Long situationId) {
        this.evaluationId = evaluationId;
        this.situationId = situationId;
    }
}
