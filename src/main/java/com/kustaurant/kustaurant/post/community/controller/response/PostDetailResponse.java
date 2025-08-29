package com.kustaurant.kustaurant.post.community.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostDetailProjection;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse (
        // 게시글
        @Schema(description = "게시글 ID", example = "1")
        Long postId,
        @Schema(description = "게시글 카테고리", example = "자유게시판")
        PostCategory category,
        @Schema(description = "게시글 제목", example = "맛집 추천해주세요")
        String title,
        @Schema(description = "게시글 내용", example = "건대 중문 근처 갈일 있는데 맛집 추천해주세요")
        String body,
        @Schema(description = "게시글에 등록된 이미지 url주소들")
        List<String> photoUrls,
        // 작성자 정보
        @Schema(description = "작성자 정보")
        @JsonUnwrapped(prefix = "writer") UserSummary writer,
        @Schema(description = "경과 시간")
        String timeAgo,
        @Schema(description = "게시글이 생성된 날짜", example = "2024-05-19T18:09:06")
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // 집계 정보
        @Schema(description = "좋아요 수")
        long likeOnlyCount,
        @Schema(description = "싫어요 수")
        long dislikeOnlyCount,
        @Schema(description = "순 좋아요 수 (좋아요-싫어요)")
        long totalLikes,
        @Schema(description = "댓글 수('DELETED' 만 제외)")
        long commentCount,
        @Schema(description = "스크랩 수")
        long scrapCount,
        @Schema(description = "방문 수")
        long visitCount,
        @Schema(description = "유저의 좋아요,싫어요 상호작용 유무 (LIKE: 좋아요, DISLIKE: 싫어요, null: 아무것도 누르지 않음)")
        ReactionType myReaction,
        // 상호작용 정보
        @Schema(description = "유저의 스크랩 유무")
        boolean isScrapped,
        @Schema(description = "본인의 게시글인지 유무")
        boolean isPostMine,
        // 댓글 정보
        @Schema(description = "댓글 목록")
        List<ParentComment> comments
){
    public static PostDetailResponse from(
            PostDetailProjection p,
            List<String> photoUrls,
            List<ParentComment> comments,
            Long currentUserId
    ) {
        String userIconUrl = UserIconResolver.resolve(p.writerEvalCount());
        String timeAgo = TimeAgoUtil.toKor(p.createdAt());
        boolean isMine = currentUserId != null && currentUserId.equals(p.writerId());
        long total = p.likeOnlyCount() - p.dislikeOnlyCount();
        UserSummary writer = new UserSummary(p.writerId(), p.writerNickName(), p.writerEvalCount(),userIconUrl);

        return new PostDetailResponse(
                p.postId(), p.category(), p.title(), p.body(), photoUrls,
                writer,
                timeAgo, p.createdAt(), p.updatedAt(),
                p.likeOnlyCount(), p.dislikeOnlyCount(), total,
                p.commentCount(), p.scrapCount(), p.visitCount(),
                p.myReaction(), p.isScrapped(), isMine,
                comments
        );
    }
}
