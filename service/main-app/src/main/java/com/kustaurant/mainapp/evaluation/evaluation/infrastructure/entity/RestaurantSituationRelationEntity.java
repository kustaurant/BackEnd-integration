package com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity;

import com.kustaurant.mainapp.evaluation.evaluation.domain.RestaurantSituationRelation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate // 변경된 필드만 Update
@Table(name = "restaurant_situation_relation")
public class RestaurantSituationRelationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    @Column(name = "situation_id")
    private Long situationId;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    private Integer dataCount;

    public static RestaurantSituationRelationEntity create(RestaurantSituationRelation relation) {
        RestaurantSituationRelationEntity entity = new RestaurantSituationRelationEntity();
        entity.situationId = relation.getSituationId();
        entity.restaurantId = relation.getRestaurantId();
        entity.dataCount = relation.getDataCount();
        return entity;
    }

    public void changeDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public RestaurantSituationRelation toModel() {
        return RestaurantSituationRelation.builder()
                .relationId(this.relationId)
                .situationId(this.situationId)
                .restaurantId(this.restaurantId)
                .dataCount(this.dataCount)
                .build();
    }
}
