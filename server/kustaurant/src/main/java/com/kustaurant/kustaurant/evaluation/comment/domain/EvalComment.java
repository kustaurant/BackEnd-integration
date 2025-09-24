package com.kustaurant.kustaurant.evaluation.comment.domain;

import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.global.exception.exception.auth.AccessDeniedException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EvalComment {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private Long evaluationId;
    private String body;
    private Integer likeCount;
    private Integer dislikeCount;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EvalComment create(Long userId, Long restaurantId, Long evaluationId, EvalCommentRequest req) {
        return EvalComment.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .evaluationId(evaluationId)
                .body(req.body())
                .likeCount(0)
                .dislikeCount(0)
                .status(Status.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void softDelete(Long requestUserId) {
        if(!userId.equals(requestUserId)) {
            throw new AccessDeniedException();
        }
        if(isDeleted()) return;
        status = Status.DELETED;
    }

    public boolean isDeleted() {
        return this.status == Status.DELETED;
    }

    public void adjustLikeCount(int num) {
        this.likeCount += num;
    }
    public void adjustDislikeCount(int num) {
        this.dislikeCount += num;
    }

}
