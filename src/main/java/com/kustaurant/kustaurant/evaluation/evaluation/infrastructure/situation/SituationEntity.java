package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationSituationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
@Table(name="situations_tbl")
public class SituationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long situationId;

    private String situationName;


    public SituationEntity(Long id, String situationName) {
        this.situationId = id;
        this.situationName = situationName;
    }
    public SituationEntity(){

    };
    @OneToMany(mappedBy = "situation")
    @JsonIgnore
    private List<RestaurantSituationRelationEntity> restaurantSituationRelationList = new ArrayList<>();

    @OneToMany(mappedBy = "situation")
    @JsonIgnore
    private List<EvaluationSituationEntity> evaluationSituationEntityList = new ArrayList<>();
}
