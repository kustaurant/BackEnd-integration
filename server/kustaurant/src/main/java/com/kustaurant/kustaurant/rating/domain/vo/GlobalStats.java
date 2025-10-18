package com.kustaurant.kustaurant.rating.domain.vo;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.lang.Nullable;

public record GlobalStats(
        Double meanSelf, Double stdSelf, // 자체 평가 평균/표준편차
        @Nullable Double meanAi, @Nullable Double stdAi, // AI 평가 점수 평균/표준편차
        @Nullable Double meanPos, @Nullable Double stdPos, // 감정 분석 긍정 비율 평균/표준편차
        @Nullable Double meanNeg, @Nullable Double stdNeg // 감정 분석 부정 비율 평균/표준편차
) {

    public boolean existsAiEvaluation() {
        return meanAi != null && stdAi != null
                && meanPos != null && stdPos != null
                && meanNeg != null && stdNeg != null;
    }

    @QueryProjection
    public GlobalStats(Double meanSelf, Double stdSelf, Double meanAi, Double stdAi, Double meanPos,
            Double stdPos, Double meanNeg, Double stdNeg) {
        this.meanSelf = meanSelf;
        this.stdSelf = stdSelf;
        this.meanAi = meanAi;
        this.stdAi = stdAi;
        this.meanPos = meanPos;
        this.stdPos = stdPos;
        this.meanNeg = meanNeg;
        this.stdNeg = stdNeg;
    }
}
