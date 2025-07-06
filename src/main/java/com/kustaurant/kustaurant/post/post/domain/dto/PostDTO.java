package com.kustaurant.kustaurant.post.post.domain.dto;

import com.kustaurant.kustaurant.post.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.user.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Slf4j
public class PostDTO {
    @Schema(description = "게시글 ID", example = "1")
    Integer postId;
    @Schema(description = "게시글 제목", example = "맛집 추천해주세요")
    String postTitle;
    @Schema(description = "게시글 내용", example = "건대 중문 근처 갈일 있는데 맛집 추천해주세요")
    String postBody;
    @Schema(description = "게시글 상태", example = "ACTIVE")
    String status;
    @Schema(description = "게시글 카테고리", example = "자유게시판")
    String postCategory;
    @Schema(description = "게시글이 생성된 날짜", example = "2024-05-19T18:09:06")
    LocalDateTime createdAt;
    @Schema(description = "게시글이 업데이트된 날짜", example = "2024-05-20T18:09:06")
    LocalDateTime updatedAt;
    @Schema(description = "총 좋아요 개수 (좋아요-싫어요)", example = "3")
    Integer likeCount;


    @Schema(description = "좋아요 개수", example = "3")
    Integer likeOnlyCount;
    @Schema(description = "싫어요 개수", example = "3")
    Integer dislikeOnlyCount;
    @Schema(description = "작성자 정보")
    UserDTO user;
    @Schema(description = "작성 경과 시간", example = "5시간 전")
    String timeAgo;
    @Schema(description = "댓글 수", example = "13")
    Integer commentCount;
    @Schema(description = "댓글 목록")
    List<PostCommentDTO> postCommentList;

    @Schema(description = "게시글 사진", example = "3")
    String postPhotoImgUrl;
    @Schema(description = "조회수", example = "3")
    Integer postVisitCount;
    @Schema(description = "스크랩 수", example = "")
    Integer scrapCount;
    @Builder.Default
    @Schema(description = "스크랩 여부", example = "false")
    Boolean isScraped = false;
    @Builder.Default
    @Schema(description = "좋아요 여부", example = "true")
    Boolean isliked = false;
    @Builder.Default
    @Schema(description = "작성자 여부", example = "true")
    Boolean isPostMine = false;

    // 레거시 메서드 제거

    public static PostDTO from(Post post) {
        return from(post, null);
    }
    // 게시글 작성자를 넣어줘야 하는 경우
    public static PostDTO from(Post post, User author) {
        PostDTO postDTO = PostDTO.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .postBody(post.getBody())
                .postCategory(post.getCategory())
                .status(post.getStatus().name())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(0) // 계산 필요시 별도 DAO로 조회
                .likeOnlyCount(0) // 계산 필요시 별도 DAO로 조회
                .dislikeOnlyCount(0) // 계산 필요시 별도 DAO로 조회
                .timeAgo(post.calculateTimeAgo())
                .postPhotoImgUrl(null) // ID 기반으로 별도 조회 필요
                .commentCount(0) // ID 기반으로 별도 조회 필요
                .postVisitCount(post.getVisitCount())
                .scrapCount(0) // ID 기반으로 별도 조회 필요
                .build();
        if (author != null) {
            postDTO.setUser(UserDTO.from(author));
        }
        return postDTO;
    }
    
    // PostDTOProjection을 활용한 최적화된 팩토리 메서드
    public static PostDTO from(PostDTOProjection projection) {
        return PostDTO.builder()
                .postId(projection.postId())
                .postTitle(projection.postTitle())
                .postBody(projection.postBody())
                .postCategory(projection.postCategory())
                .status(projection.status())
                .createdAt(projection.createdAt())
                .updatedAt(projection.updatedAt())
                .likeCount(projection.getNetLikes())
                .likeOnlyCount(projection.getLikeOnlyCount())
                .dislikeOnlyCount(projection.getDislikeOnlyCount())
                .user(createUserDTO(projection))
                .timeAgo(calculateTimeAgo(projection.createdAt()))
                .postPhotoImgUrl(projection.firstPhotoUrl())
                .commentCount(projection.getCommentCount())
                .postVisitCount(projection.visitCount())
                .scrapCount(projection.getScrapCount())
                .isScraped(projection.isScraped())
                .isliked(projection.isLiked())
                .isPostMine(false) // 별도 로직으로 설정
                .build();
    }
    
    // 시간 경과 계산 헬퍼 메서드
    private static String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long diffInMinutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (diffInMinutes < 1) {
            return "방금 전";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + "분 전";
        } else if (diffInMinutes < 1440) { // 24시간
            return (diffInMinutes / 60) + "시간 전";
        } else {
            return (diffInMinutes / 1440) + "일 전";
        }
    }
    
    // UserDTO 생성 헬퍼 메서드
    private static UserDTO createUserDTO(PostDTOProjection projection) {
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(projection.authorNickname());
        userDTO.setRankImg(projection.authorRankImg());
        userDTO.setEvaluationCount(projection.authorEvaluationCount());
        return userDTO;
    }
}
