package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name="situations_tbl")
@NoArgsConstructor
public class SituationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long situationId;

    private String situationName;
}
