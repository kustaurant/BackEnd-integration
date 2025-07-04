package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "restaurants_tbl")
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer restaurantId;


    private String restaurantName;
    private String restaurantType;
    private String restaurantPosition;
    private String restaurantAddress;
    private String restaurantTel;
    @Column(unique = true)
    private String restaurantUrl;
    private String restaurantImgUrl;
    private Integer restaurantVisitCount = 0;
    private Integer visitCount = 0;
    private Integer restaurantEvaluationCount = 0;
    private Double restaurantScoreSum = 0d;
    private Integer mainTier = -1;

    private String restaurantCuisine;
    private String restaurantLatitude;
    private String restaurantLongitude;
    private String partnershipInfo;

    private String status;
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;


    // 다른 테이블과의 관계 매핑
    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<EvalCommentEntity> restaurantCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RestaurantFavoriteEntity> restaurantFavorite = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RestaurantMenuEntity> restaurantMenuList = new ArrayList<>();

    public void updateStatistics(Restaurant restaurant) {
        this.restaurantVisitCount = restaurant.getRestaurantVisitCount();
        this.visitCount = restaurant.getVisitCount();
        this.restaurantEvaluationCount = restaurant.getRestaurantEvaluationCount();
        this.restaurantScoreSum = restaurant.getRestaurantScoreSum();
        this.mainTier = restaurant.getMainTier();
    }

    public static RestaurantEntity fromDomain(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setRestaurantId(restaurant.getRestaurantId());
        entity.setRestaurantName(restaurant.getRestaurantName());
        entity.setRestaurantType(restaurant.getRestaurantType());
        entity.setRestaurantPosition(restaurant.getRestaurantPosition());
        entity.setRestaurantAddress(restaurant.getRestaurantAddress());
        entity.setRestaurantTel(restaurant.getRestaurantTel());
        entity.setRestaurantUrl(restaurant.getRestaurantUrl());
        entity.setRestaurantImgUrl(restaurant.getRestaurantImgUrl());
        entity.setRestaurantCuisine(restaurant.getRestaurantCuisine());
        entity.setRestaurantLatitude(restaurant.getRestaurantLatitude());
        entity.setRestaurantLongitude(restaurant.getRestaurantLongitude());
        entity.setPartnershipInfo(restaurant.getPartnershipInfo());
        entity.setStatus(restaurant.getStatus());
        entity.setCreatedAt(restaurant.getCreatedAt());
        entity.setUpdatedAt(restaurant.getUpdatedAt());
        entity.setRestaurantVisitCount(restaurant.getRestaurantVisitCount());
        entity.setVisitCount(restaurant.getVisitCount());
        entity.setRestaurantEvaluationCount(restaurant.getRestaurantEvaluationCount());
        entity.setRestaurantScoreSum(restaurant.getRestaurantScoreSum());
        entity.setMainTier(restaurant.getMainTier());

        return entity;
    }

    public Restaurant toDomain() {
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName(restaurantName)
                .restaurantType(restaurantType)
                .restaurantPosition(restaurantPosition)
                .restaurantAddress(restaurantAddress)
                .restaurantTel(restaurantTel)
                .restaurantUrl(restaurantUrl)
                .restaurantImgUrl(restaurantImgUrl)
                .restaurantCuisine(restaurantCuisine)
                .restaurantLatitude(restaurantLatitude)
                .restaurantLongitude(restaurantLongitude)
                .partnershipInfo(partnershipInfo)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .restaurantVisitCount(restaurantVisitCount)
                .visitCount(visitCount)
                .restaurantEvaluationCount(restaurantEvaluationCount)
                .restaurantScoreSum(restaurantScoreSum)
                .mainTier(mainTier)
                // TODO: 이거 복구해야됨 (2025-06-30)
//                .situations(restaurantSituationRelationEntityList.stream().filter(RestaurantChartSpec::hasSituation).map(el -> el.getSituation().getSituationName()).collect(Collectors.toList()))
                .favoriteCount(this.restaurantFavorite.size())
                .build();
    }
}