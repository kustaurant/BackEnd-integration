package com.kustaurant.kustaurant.rating.domain.vo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "restaurant.rating.policy.score")
public record ScorePolicyProp(
        @Min(0) int priorWeight,
        @Valid EvaluationProp evaluation,
        @Valid RestaurantProp restaurant
) {
    public record EvaluationProp(
            @DecimalMin("1.0") double reactionDivisor,
            @DecimalMax("1.0") @DecimalMin("0.0") double reactionScale,
            @Min(1) int evaluatorK,
            @Valid CompletenessWeight completenessWeight
    ) {

        public double getReactionW(long reactionScore) {
            double score = Math.max(-reactionDivisor, Math.min(reactionDivisor, reactionScore));
            return 1 + reactionScale * Math.tanh(score / reactionDivisor);
        }

        public double getEvaluationReliabilityW(long evalCount) {
            Assert.isTrue(evalCount > 0, "평가 수는 1 이상이어야 합니다.");
            return evalCount / (double)(evalCount + evaluatorK);
        }

        public record CompletenessWeight(
                @DecimalMin("0.1") @DecimalMax("1.0") double score,
                @DecimalMin("0.0") @DecimalMax("1.0") double comment,
                @DecimalMin("0.0") @DecimalMax("1.0") double situation,
                @DecimalMin("0.0") @DecimalMax("1.0") double image
        ) {
            public double getWeightSum() {
                return score + comment + situation + image;
            }

            public double getCompletenessW(boolean existComment, boolean existSituation, boolean existImage) {
                return score
                        + comment * bool(existComment)
                        + situation * bool(existSituation)
                        + image * bool(existImage);
            }

            private double bool(boolean b) {
                return b ? 1 : 0;
            }
        }
    }

    public record AiProp(

    ) {}

    public record RestaurantProp(
            @Min(1) int minEvaluationCnt,
            @Min(1) int manyEvaluationCnt,
            @Min(1) int evaluationCntPriorStrengthK
    ) {

        public boolean hasEnoughEvaluations(int count) {
            return count >= minEvaluationCnt;
        }

        public boolean hasManyEvaluations(int count) {
            return count >= manyEvaluationCnt;
        }

        public double calculateScore(int evalCnt, double selfScore, double aiScore) {
            if (evalCnt < 1) return aiScore;
            if (selfScore <= 0.0) return aiScore;
            if (aiScore <= 0.0) return selfScore;
            double selfRatio;
            if (!hasEnoughEvaluations(evalCnt)) {
                selfRatio = evalCnt / 8.0;
            } else if (!hasManyEvaluations(evalCnt)) {
                selfRatio = 0.6 + 0.06 * (evalCnt - minEvaluationCnt);
            } else {
                selfRatio = 0.95;
            }
            return selfScore * selfRatio + aiScore * (1 - selfRatio);
        }

        public double shrinkByEvaluationCount(double score, double avg, long evaluationCnt) {
            Assert.isTrue(evaluationCnt >= 0, "evaluationCnt는 0 이상이어야 합니다.");
            double z = evaluationCnt / (double) (evaluationCnt + evaluationCntPriorStrengthK);
            return score * z + (1 - z) * avg;
        }
    }
}
