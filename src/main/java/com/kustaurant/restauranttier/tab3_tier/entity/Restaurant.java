
package com.kustaurant.restauranttier.tab3_tier.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "restaurants_tbl")
public class Restaurant {
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

    public Restaurant(String restaurantName, String restaurantType, String restaurantUrl, Integer visitCount,String restaurantCuisine, String status, LocalDateTime createdAt) {
        this.restaurantName = restaurantName;
        this.restaurantType = restaurantType;
        this.restaurantUrl = restaurantUrl;
        this.restaurantVisitCount=visitCount;
        this.restaurantCuisine = restaurantCuisine;
        this.status = status;
        this.createdAt = createdAt;
    }

    @JsonIgnore
    private LocalDateTime updatedAt;


    public Restaurant(){

    }
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

    public String getTierImgUrl(Integer tier) {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/" + tier.toString() + "tier.png";
    }
    public String getCuisineImgUrl(String cuisine) {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/cuisine-icon/" + cuisine.replaceAll("/", "") + ".svg";
    }

    // 평균 평가 점수 계산
    public double calculateAverageScore() {
        if (evaluationList.isEmpty()) {
            return 0.0; // 평가가 없는 경우 0 반환
        }

        return evaluationList.stream()
                .mapToDouble(Evaluation::getEvaluationScore) // 평가 점수로 변환
                .average() // 평균 계산
                .orElse(0.0); // 평가가 없는 경우 0 반환
    }

    // 환산 점수 계산
    public String getMainScoreMaxTen() {
        double score = (this.restaurantScoreSum / this.restaurantEvaluationCount) / 7 * 10;
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(score) + "/10.0";
    }
}

