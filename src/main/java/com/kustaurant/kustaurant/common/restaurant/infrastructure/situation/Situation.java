package com.kustaurant.kustaurant.common.restaurant.infrastructure.situation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationItemScore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Entity
@Setter
@Table(name="situations_tbl")
public class Situation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer situationId;

    private String situationName;


    public Situation(Integer id, String situationName) {
        this.situationId=id;
        this.situationName = situationName;
    }
    public Situation(){

    };
    @OneToMany(mappedBy = "situation")
    @JsonIgnore
    private List<RestaurantSituationRelation> restaurantSituationRelationList = new ArrayList<>();

    @OneToMany(mappedBy = "situation")
    @JsonIgnore
    private List<EvaluationItemScore> evaluationItemScoreList = new ArrayList<>();
}
