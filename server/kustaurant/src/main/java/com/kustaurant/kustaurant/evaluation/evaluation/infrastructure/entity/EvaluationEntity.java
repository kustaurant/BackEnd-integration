package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@Table(name="evaluation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluationEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long id;

    private Double evaluationScore;
    private String status;
    // 평가 내용 관련
    private String body;
    private String imgUrl;
    private Integer likeCount;
    private Integer dislikeCount;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @ElementCollection
    @CollectionTable(
            name = "evaluation_situation",
            joinColumns = @JoinColumn(name = "evaluation_id")
    )
    @Column(name = "situation_id")
    private List<Long> situationIds = new ArrayList<>();

    @Builder
    public EvaluationEntity(
            Double evaluationScore,
            String status,
            String body,
            String imgUrl,
            Integer likeCount,
            Integer dislikeCount,
            Long userId,
            Long restaurantId
    ) {
        this.evaluationScore = evaluationScore;
        this.userId = userId;
        this.status = status;
        this.body = body;
        this.imgUrl = imgUrl;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.restaurantId = restaurantId;
    }

    public static EvaluationEntity from(Evaluation evaluation) {
        EvaluationEntity entity = new EvaluationEntity();
        entity.id = evaluation.getId();
        entity.evaluationScore = evaluation.getEvaluationScore();
        entity.status = evaluation.getStatus();
        entity.body = evaluation.getCommentBody();
        entity.imgUrl = evaluation.getCommentImgUrl();
        entity.likeCount = evaluation.getLikeCount();
        entity.dislikeCount = evaluation.getDislikeCount();
        entity.userId = evaluation.getUserId();
        entity.restaurantId = evaluation.getRestaurantId();
        entity.situationIds = new ArrayList<>(evaluation.getSituationIds());
        return entity;
    }

    public void reEvaluate(Evaluation evaluation) {
        this.evaluationScore = evaluation.getEvaluationScore();
        this.body = evaluation.getCommentBody();
        this.imgUrl = evaluation.getCommentImgUrl();
        updateSituations(evaluation.getSituationIds());
    }

    public void updateSituations(List<Long> newSituationIds) {
        this.situationIds.clear();
        this.situationIds.addAll(newSituationIds);
    }

    public void react(Integer likeCount, Integer dislikeCount) {
        if (likeCount != null && likeCount >= 0) {
            this.likeCount = likeCount;
        }
        if (dislikeCount != null && dislikeCount >= 0) {
            this.dislikeCount = dislikeCount;
        }
    }

    public Evaluation toModel() {
        return Evaluation.builder()
                .id(this.id)
                .evaluationScore(this.evaluationScore)
                .status(this.status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .commentBody(this.body)
                .commentImgUrl(this.imgUrl)
                .situationIds(this.situationIds)
                .userId(this.userId)
                .restaurantId(this.restaurantId)
                .likeCount(this.likeCount)
                .dislikeCount(this.dislikeCount)
                .build();
    }

    public String getStarImgUrl() { // web에서 사용중
        String[] scoreSplit = evaluationScore.toString().split("\\.");
        if (scoreSplit.length > 1) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + scoreSplit[1] + ".svg";
        } else {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + "0.svg";
        }
    }
}
