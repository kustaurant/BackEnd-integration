package com.kustaurant.mainapp.user.rank.infrastructure;

public interface UserRankProjection {
    Long getUserId();
    String getNickname();
    Integer getEvaluationCount();
    Integer getUserRank();
}
