package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    @Schema(description = "작성자 정보")
    UserDTO user;
    @Schema(description = "작성 경과 시간",example = "5시간 전")
    String timeAgo;
    @Schema(description = "댓글 수",example = "13")
    Integer commentCount;
    @Schema(description = "댓글 목록")
    List<PostCommentDTO> postCommentList;

    @Schema(description = "게시글 사진", example = "3")
    String postPhotoImgUrl;
    @Schema(description = "조회수", example = "3")
    Integer postVisitCount;
    @Schema(description = "스크랩 수", example = "")
    Integer scrapCount;
    @Schema(description = "스크랩 여부", example = "false")
    Boolean isScraped =false;
    @Schema(description = "좋아요 여부", example = "true")
    Boolean isliked =false;
    @Schema(description = "작성자 여부",example = "true")
    Boolean isPostMine =false;

    public static PostDTO convertPostToPostDTO(PostEntity postEntity, Integer likeCount) {
        PostDTO dto = new PostDTO();
        dto.setPostId(postEntity.getPostId());
        dto.setPostTitle(postEntity.getPostTitle());
        dto.setPostBody(postEntity.getPostBody());
        dto.setStatus(postEntity.getStatus());
        dto.setPostCategory(postEntity.getPostCategory());
        dto.setCreatedAt(postEntity.getCreatedAt());
        dto.setUpdatedAt(postEntity.getUpdatedAt());
        dto.setLikeCount(likeCount);
        dto.setUser(UserDTO.convertUserToUserDTO(postEntity.getUser()));
        int commentCount = postEntity.getPostCommentList().stream().filter(c -> c.getStatus().equals("ACTIVE")).toList().size();
        dto.setCommentCount(commentCount);
        dto.setTimeAgo(postEntity.toDomain().calculateTimeAgo());
        if(!postEntity.getPostPhotoList().isEmpty()){
            dto.setPostPhotoImgUrl(postEntity.getPostPhotoList().get(0).getPhotoImgUrl());
        }else{
            dto.setPostPhotoImgUrl(null);
        }
        dto.setPostVisitCount(postEntity.getPostVisitCount());
        dto.setScrapCount(postEntity.getPostScrapList().size());
        return dto;
    }
}
