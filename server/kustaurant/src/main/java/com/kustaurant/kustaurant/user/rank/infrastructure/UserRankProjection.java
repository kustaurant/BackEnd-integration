package com.kustaurant.kustaurant.user.rank.infrastructure;

public interface UserRankProjection {
    Long getUserId();
    String getNickname();
    Integer getEvaluationCount();
    Integer getUserRank();
}
