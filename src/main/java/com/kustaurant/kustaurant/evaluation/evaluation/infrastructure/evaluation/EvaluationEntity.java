package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.EvaluationSituationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
// 한 사용자가 한 식당을 중복 평가 할 수 없음
@Table(name="evaluations_tbl")
@NoArgsConstructor
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    private Double evaluationScore;
    private String status = "ACTIVE";
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount = 0;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    // 상황 일대다 단방향 매핑
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "evaluation_id")
    private List<EvaluationSituationEntity> evaluationSituations = new ArrayList<>();

    // 일단 둠
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentLikeEntity> restaurantCommentLikeList = new ArrayList<>();
    @OneToMany(mappedBy = "evaluation")
    private List<RestaurantCommentDislikeEntity> restaurantCommentDislikeList = new ArrayList<>();

    public EvaluationEntity(Double evaluationScore, String status, LocalDateTime createdAt, String commentBody, String commentImgUrl, Long userId, Integer restaurantId) {
        this.evaluationScore = evaluationScore;
        this.status = status;
        this.createdAt = createdAt;
        this.commentBody = commentBody;
        this.commentImgUrl = commentImgUrl;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public Evaluation toModel() {
        return Evaluation.builder()
                .id(this.id)
                .evaluationScore(this.evaluationScore)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .commentBody(this.commentBody)
                .commentImgUrl(this.commentImgUrl)
                .commentLikeCount(this.commentLikeCount == null ? 0 : this.commentLikeCount)
                .situationIds(this.evaluationSituations.stream()
                        .map(EvaluationSituationEntity::getSituationId)
                        .toList())
                .userId(this.userId)
                .restaurantId(this.restaurantId)
                .likeCount(this.restaurantCommentLikeList == null ? 0 : this.restaurantCommentLikeList.size())
                .dislikeCount(this.restaurantCommentDislikeList == null ? 0 : this.restaurantCommentDislikeList.size())
                .build();
    }

    public static EvaluationEntity from(Evaluation domain) {
        EvaluationEntity entity = new EvaluationEntity();
        entity.id = domain.getId();
        entity.evaluationScore = domain.getEvaluationScore();
        entity.status = domain.getStatus();
        entity.createdAt = domain.getCreatedAt();
        entity.updatedAt = domain.getUpdatedAt();
        entity.commentBody = domain.getCommentBody();
        entity.commentImgUrl = domain.getCommentImgUrl();
        entity.commentLikeCount = domain.getCommentLikeCount();
        entity.userId = domain.getUserId();
        entity.restaurantId = domain.getRestaurantId();
        return entity;
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
}
