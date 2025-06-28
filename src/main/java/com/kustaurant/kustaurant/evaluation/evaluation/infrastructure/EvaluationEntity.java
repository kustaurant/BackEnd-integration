package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDomain;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationSituation;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Situation;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@Setter
// 한 사용자가 한 식당을 중복 평가 할 수 없음
@Table(name="evaluations_tbl")
public class EvaluationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "evaluation_id")
    private Integer id;

    private Double evaluationScore;
    private String status="ACTIVE";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount = 0;
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentLikeEntity> restaurantCommentLikeList = new ArrayList<>();
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentDislikeEntity> restaurantCommentDislikeList = new ArrayList<>();

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "evaluation")
    private List<EvaluationSituationEntity> evaluationSituationEntityList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentReportEntity> restaurantCommentReportList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentEntity> restaurantCommentList = new ArrayList<>();
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    public EvaluationEntity() {
    }

    public EvaluationEntity(RestaurantEntity restaurant, Long userId, Double evaluationScore) {
        this.restaurant = restaurant;
        this.userId = userId;
        this.evaluationScore = evaluationScore;
        this.createdAt = LocalDateTime.now();
    }

    public EvaluationEntity(Double evaluationScore, String status, LocalDateTime createdAt, String commentBody, String commentImgUrl, Long userId, RestaurantEntity restaurant) {
        this.evaluationScore = evaluationScore;
        this.status = status;
        this.createdAt = createdAt;
        this.commentBody = commentBody;
        this.commentImgUrl = commentImgUrl;
        this.userId = userId;
        this.restaurant = restaurant;
    }

    // 평가에서 선택한 situation들의 id 리스트를 반환합니다. 이전에 선택한게 없을경우 null을 반환합니다.
    public List<Integer> getSituationIdList() {
        if (this.evaluationSituationEntityList == null || this.evaluationSituationEntityList.isEmpty()) {
            return null;
        }

        return this.evaluationSituationEntityList.stream()
                .map(evaluationItemScore -> evaluationItemScore.getSituation().getSituationId())
                .toList();
    }

    public String getStarImgUrl() {
        try {
            String[] scoreSplit = evaluationScore.toString().split("\\.");
            if (scoreSplit.length > 1) {
                return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + scoreSplit[1] + ".svg";
            } else {
                return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + "0.svg";
            }
        } catch (Exception e) {
            return null;
        }
    }

    public EvaluationDomain toModel() {
        return EvaluationDomain.builder()
                .evaluationId(this.id)
                .evaluationScore(this.evaluationScore)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .commentBody(this.commentBody)
                .commentImgUrl(this.commentImgUrl)
                .commentLikeCount(this.commentLikeCount)
                .userId(this.userId)
                .restaurant(this.restaurant.toDomain())
                .evaluationSituationList(
                        this.evaluationSituationEntityList.stream()
                                .map(entity -> EvaluationSituation.builder()
                                        .situation(Situation.builder()
                                                .situationId(entity.getSituation().getSituationId())
                                                .situationName(entity.getSituation().getSituationName())
                                                .build())
                                        .build()
                                ).collect(Collectors.toList())
                )
                .build();
    }

    public static EvaluationEntity from(EvaluationDomain domain) {
        EvaluationEntity entity = new EvaluationEntity();
        entity.id = domain.getEvaluationId();
        entity.evaluationScore = domain.getEvaluationScore();
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        entity.commentBody = domain.getCommentBody();
        entity.commentImgUrl = domain.getCommentImgUrl();
        entity.userId = domain.getUserId();
        entity.restaurant = RestaurantEntity.fromDomain(domain.getRestaurant());
        return entity;
    }


}
