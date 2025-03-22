package com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantComment;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.RestaurantSpecification;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.*;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.hashtag.RestaurantHashtag;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.menu.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.situation.RestaurantSituationRelation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
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
    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "restaurant_hashtag_relations_tbl", joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name="hashtag_id"))
    List<RestaurantHashtag> restaurantHashtagList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    List<RestaurantSituationRelation> restaurantSituationRelationList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<Evaluation> evaluationList=new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RestaurantComment> restaurantCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RestaurantFavorite> restaurantFavorite = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    @JsonIgnore
    private List<RestaurantMenu> restaurantMenuList = new ArrayList<>();

    public double calculateAverageScore() {
        if (evaluationList.isEmpty()) {
            return 0.0; // 평가가 없는 경우 0 반환
        }

        return evaluationList.stream()
                .mapToDouble(Evaluation::getEvaluationScore) // 평가 점수로 변환
                .average() // 평균 계산
                .orElse(0.0); // 평가가 없는 경우 0 반환
    }

    public static RestaurantEntity from(RestaurantDomain restaurantDomain) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setRestaurantId(restaurantDomain.getRestaurantId());
        entity.setRestaurantName(restaurantDomain.getRestaurantName());
        entity.setRestaurantType(restaurantDomain.getRestaurantType());
        entity.setRestaurantPosition(restaurantDomain.getRestaurantPosition());
        entity.setRestaurantAddress(restaurantDomain.getRestaurantAddress());
        entity.setRestaurantTel(restaurantDomain.getRestaurantTel());
        entity.setRestaurantUrl(restaurantDomain.getRestaurantUrl());
        entity.setRestaurantImgUrl(restaurantDomain.getRestaurantImgUrl());
        entity.setRestaurantCuisine(restaurantDomain.getRestaurantCuisine());
        entity.setRestaurantLatitude(restaurantDomain.getRestaurantLatitude());
        entity.setRestaurantLongitude(restaurantDomain.getRestaurantLongitude());
        entity.setPartnershipInfo(restaurantDomain.getPartnershipInfo());
        entity.setStatus(restaurantDomain.getStatus());
        entity.setCreatedAt(restaurantDomain.getCreatedAt());
        entity.setUpdatedAt(restaurantDomain.getUpdatedAt());
        entity.setRestaurantVisitCount(restaurantDomain.getRestaurantVisitCount());
        entity.setVisitCount(restaurantDomain.getVisitCount());
        entity.setRestaurantEvaluationCount(restaurantDomain.getRestaurantEvaluationCount());
        entity.setRestaurantScoreSum(restaurantDomain.getRestaurantScoreSum());
        entity.setMainTier(restaurantDomain.getMainTier());

        return entity;
    }

    public RestaurantDomain toModel() {
        return RestaurantDomain.builder()
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
                // TODO: 개선 필요해 보임
                .situations(restaurantSituationRelationList.stream().filter(RestaurantSpecification::hasSituation).map(el -> el.getSituation().getSituationName()).collect(Collectors.toList()))
                .favoriteCount(this.restaurantFavorite.size())
                .build();
    }
}