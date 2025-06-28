package com.kustaurant.kustaurant.evaluation.comment.infrastructure.projection;

import java.time.LocalDateTime;

public interface EvalCommentProjection {
    // 댓글·평가 자체정보
    Integer getCommentId();
    Double  getScore();
    String  getBody();
    String        getImgUrl();
    LocalDateTime getWrittenAt();

    // 유저 정보
    Long    getUserId();
    String  getNickname();
    Integer  getRatedCnt();
    Boolean getLikedByMe();
    Boolean getDislikedByMe();

    // 추천·비추천 집계
    Integer getLikeCnt();
    Integer getDislikeCnt();
}
