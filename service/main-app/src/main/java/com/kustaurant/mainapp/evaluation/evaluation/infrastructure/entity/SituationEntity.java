package com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name="restaurant_situation")
@NoArgsConstructor
public class SituationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long situationId;

    private String situationName;
}
