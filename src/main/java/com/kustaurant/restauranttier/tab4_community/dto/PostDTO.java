package com.kustaurant.restauranttier.tab4_community.dto;

import com.kustaurant.restauranttier.tab4_community.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
    @Schema(description = "좋아요 개수", example = "3")

    Integer likeCount;
    UserDTO user;

    public static PostDTO fromEntity(Post post) {
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setPostTitle(post.getPostTitle());
        dto.setPostBody(post.getPostBody());
        dto.setStatus(post.getStatus());
        dto.setPostCategory(post.getPostCategory());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikeCount(post.getLikeCount());
        dto.setUser(UserDTO.fromEntity(post.getUser()));
        return dto;
    }
}
