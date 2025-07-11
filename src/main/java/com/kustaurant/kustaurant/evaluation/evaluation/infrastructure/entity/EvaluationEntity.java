package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String body;
    private String imgUrl;
    private Integer likeCount;
    private Integer dislikeCount;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "restaurant_id", nullable = false)
    private Integer restaurantId;

    // Evaluation 에서 EvaluationSituation 관리
    @ElementCollection
    @CollectionTable(
            name = "evaluation_situations_tbl",
            joinColumns = @JoinColumn(name = "evaluation_id")
    )
    @Column(name = "situation_id")
    private List<Long> situationIds = new ArrayList<>();

    public EvaluationEntity(Double evaluationScore, String status, LocalDateTime createdAt, String body, String imgUrl, Integer likeCount, Integer dislikeCount, Long userId, Integer restaurantId) {
        this.evaluationScore = evaluationScore;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
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
        entity.createdAt = evaluation.getCreatedAt();
        entity.updatedAt = evaluation.getUpdatedAt();
        entity.body = evaluation.getCommentBody();
        entity.imgUrl = evaluation.getCommentImgUrl();
        entity.likeCount = evaluation.getCommentLikeCount();
        entity.userId = evaluation.getUserId();
        entity.restaurantId = evaluation.getRestaurantId();
        entity.situationIds = new ArrayList<>(evaluation.getSituationIds());
        return entity;
    }

    public void reEvaluate(Evaluation evaluation) {
        this.evaluationScore = evaluation.getEvaluationScore();
        this.updatedAt = evaluation.getUpdatedAt();
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
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .commentBody(this.body)
                .commentImgUrl(this.imgUrl)
                .commentLikeCount(this.likeCount == null ? 0 : this.likeCount)
                .situationIds(this.situationIds)
                .userId(this.userId)
                .restaurantId(this.restaurantId)
                .likeCount(this.likeCount)
                .dislikeCount(this.dislikeCount)
                .build();
    }

    public String getStarImgUrl() {
        String[] scoreSplit = evaluationScore.toString().split("\\.");
        if (scoreSplit.length > 1) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + scoreSplit[1] + ".svg";
        } else {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/star/star" + scoreSplit[0] + "0.svg";
        }
    }
}
