package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate // 변경된 필드만 Update
// 한 사용자가 한 식당을 중복 평가 할 수 없음
@Table(name="evaluations_tbl")
@NoArgsConstructor
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    private Double evaluationScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 평가 내용 관련
    private String commentBody;
    private String commentImgUrl;
    private Integer commentLikeCount;

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

    public static EvaluationEntity create(Evaluation evaluation) {
        EvaluationEntity entity = new EvaluationEntity();
        entity.evaluationScore = evaluation.getEvaluationScore();
        entity.status = evaluation.getStatus();
        entity.createdAt = evaluation.getCreatedAt();
        entity.commentBody = evaluation.getCommentBody();
        entity.commentImgUrl = evaluation.getCommentImgUrl();
        entity.commentLikeCount = evaluation.getCommentLikeCount();
        entity.userId = evaluation.getUserId();
        entity.restaurantId = evaluation.getRestaurantId();
        return entity;
    }

    public void reEvaluate(Evaluation evaluation) {
        this.evaluationScore = evaluation.getEvaluationScore();
        this.updatedAt = evaluation.getUpdatedAt();
        this.commentBody = evaluation.getCommentBody();
        this.commentImgUrl = evaluation.getCommentImgUrl();
        updateSituations(evaluation.getSituationIds());
    }

    public void updateSituations(List<Long> situationIds) {
        this.evaluationSituations.clear();
        situationIds.stream()
                .map(s -> new EvaluationSituationEntity(this.id, s))
                .forEach(evaluationSituations::add);
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
