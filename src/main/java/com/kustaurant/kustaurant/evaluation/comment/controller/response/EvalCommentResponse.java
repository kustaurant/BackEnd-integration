package com.kustaurant.kustaurant.evaluation.comment.controller.response;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record EvalCommentResponse(
        @Schema(description = "코멘트 id", example = "3")
        Integer commentId,
        @Schema(description = "평가 별점", example = "4.5")
        Double commentScore,
        @Schema(description = "유저 평가 등급에 따른 이미지 url")
        String writerIconImgUrl,
        @Schema(description = "닉네임", example = "역병")
        String writerNickname,
        @Schema(description = "시간 정보", example = "2분전")
        String timeAgo,
        @Schema(description = "평가에 유저가 첨부한 이미지 url")
        String commentImgUrl,
        @Schema(description = "평가 멘트", example = "오 좀 맛있는데?")
        String commentBody,
        @Schema(description = "유저의 추천 비추천 여부 (1=추천 누름, 0=아무것도 안누름, -1=비추천 누름)", example = "1")
        Integer commentLikeStatus,
        @Schema(description = "추천 개수", example = "14")
        Integer commentLikeCount,
        @Schema(description = "비추천 개수", example = "3")
        Integer commentDislikeCount,
        @Schema(description = "사용자가 단 댓글인지 여부", example = "true")
        Boolean isCommentMine,
        @Schema(description = "대댓글 리스트")
        List<EvalCommentResponse> commentReplies
) {
        /** 대댓글을 셋팅한 ‘새 DTO’ 반환 */
        public EvalCommentResponse withCommentReplies(List<EvalCommentResponse> replies) {
                return new EvalCommentResponse(
                        commentId, commentScore, writerIconImgUrl, writerNickname,
                        timeAgo, commentImgUrl, commentBody, commentLikeStatus,
                        commentLikeCount, commentDislikeCount, isCommentMine, replies
                );
        }

        /** (optional) 좋아요-싫어요 차이 */
        public int commentLikeDiffDislike() {
                return commentLikeCount - commentDislikeCount;
        }

        /** (optional) 정렬용 날짜 — timeAgo 대신 LocalDateTime 두고 계산 추천 */
        public LocalDateTime getDate() {
                // 예시: timeAgo 를 계산할 때 썼던 원본 createdAt/updatedAt 을 저장해 두는 편이 안전
                throw new UnsupportedOperationException("getDate 구현 필요");
        }
    public static EvalCommentResponse convertCommentWhenEvaluation(
            EvaluationEntity evaluation,
            Long userId
    ) {
        return new EvalCommentResponse(
                evaluation.getId() + EvaluationConstants.EVALUATION_ID_OFFSET,
                evaluation.getEvaluationScore(),
                evaluation.getUser().getNickname().getValue(),
                TimeAgoUtil.toKor(evaluation.getCreatedAt()),
                evaluation.getCommentImgUrl(),
                evaluation.getCommentBody(),
                isUserLikeDisLikeStatus(evaluation, userId),
                evaluation.getRestaurantCommentLikeList().size(),
                evaluation.getRestaurantCommentDislikeList().size(),
                isCommentMine(userId, evaluation),
                null,
                evaluation.getUpdatedAt() == null ? evaluation.getCreatedAt() : evaluation.getUpdatedAt(),
                evaluation.getUserId(),
                evaluation
        );
    }

    public static EvalCommentResponse convertCommentWhenSubComment(
            RestaurantCommentEntity comment,
            Double evaluationScore,
            Long userId
    ) {

        return new EvalCommentResponse(
                comment.getCommentId(),
                evaluationScore,
                RestaurantConstants.getIconImgUrl(comment.getUser()),
                comment.getUser().getNickname().getValue(),
                comment.calculateTimeAgo(),
                null,
                comment.getCommentBody(),
                isUserLikeDisLikeStatus(comment, userId),
                comment.getRestaurantCommentLikeList().size(),
                comment.getRestaurantCommentDislikeList().size(),
                isCommentMine(userId, comment),
                null,
                comment.getUpdatedAt() == null ? comment.getCreatedAt() : comment.getUpdatedAt(),
                comment.getUserId(),
                null
        );
    }

    // 추천수와 비추천 수만 반환하는 생성자
    public static EvalCommentResponse convertCommentWhenLikeDislike(RestaurantCommentEntity comment, Long userId) {
        return new EvalCommentResponse(
                null, null, null, null, null, null, null,
                isUserLikeDisLikeStatus(comment, userId),
                comment.getRestaurantCommentLikeList().size(),
                comment.getRestaurantCommentDislikeList().size(), null, null
        );
    }
    public static EvalCommentResponse convertCommentWhenLikeDislike(EvaluationEntity evaluation, Long userId) {
        return new EvalCommentResponse(
                null, null, null, null, null, null, null,
                isUserLikeDisLikeStatus(evaluation, userId),
                evaluation.getRestaurantCommentLikeList().size(),
                evaluation.getRestaurantCommentDislikeList().size(), null, null
        );
    }

    public int commentLikeDiffDislike() {
        return commentLikeCount - commentDislikeCount;
    }


    public static boolean isCommentMine(Long userId, EvaluationEntity evaluation) {
        if (userId == null) {
            return false;
        }
        return evaluation.getUserId().equals(userId);
    }

    public static boolean isCommentMine(Long userId, RestaurantCommentEntity comment) {
        if (userId == null) {
            return false;
        }
        return comment.getUserId().equals(userId);
    }

    private static int isUserLikeDisLikeStatus(EvaluationEntity evaluation, Long userId) {
        // 유저가 좋아요를 눌렀는지 확인
        boolean liked = evaluation.getRestaurantCommentLikeList().stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        if (liked) {
            return 1;
        }

        // 유저가 싫어요를 눌렀는지 확인
        boolean disliked = evaluation.getRestaurantCommentDislikeList().stream()
                .anyMatch(dislike -> dislike.getUserId().equals(userId));

        return disliked ? -1 : 0;
    }

    private static int isUserLikeDisLikeStatus(RestaurantCommentEntity comment, Long userId) {
        // 유저가 좋아요를 눌렀는지 확인
        boolean liked = comment.getRestaurantCommentLikeList().stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        if (liked) {
            return 1;
        }

        // 유저가 싫어요를 눌렀는지 확인
        boolean disliked = comment.getRestaurantCommentDislikeList().stream()
                .anyMatch(dislike -> dislike.getUserId().equals(userId));

        return disliked ? -1 : 0;
    }
}
