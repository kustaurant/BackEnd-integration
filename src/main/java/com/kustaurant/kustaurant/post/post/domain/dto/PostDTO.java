package com.kustaurant.kustaurant.post.post.domain.dto;

import com.kustaurant.kustaurant.post.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
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

//    public static PostDTO convertPostToPostDTO(PostEntity postEntity) {
//        return PostDTO.builder()
//                .postId(postEntity.getPostId())
//                .postTitle(postEntity.getPostTitle())
//                .postBody(postEntity.getPostBody())
//                .status(postEntity.getStatus().name())
//                .postCategory(postEntity.getPostCategory())
//                .createdAt(postEntity.getCreatedAt())
//                .updatedAt(postEntity.getUpdatedAt())
//                .likeCount(postEntity.getNetLikes())
//                .likeOnlyCount(postEntity.getPostLikesList().size())
//                .dislikeOnlyCount(postEntity.getPostDislikesList().size())
//                .user(UserDTO.convertUserToUserDTO(postEntity.getUserId()))
//                .commentCount((int) postEntity.getPostCommentList().stream()
//                        .filter(c -> c.getStatus().equals(ContentStatus.ACTIVE))
//                        .count())
//                .timeAgo(postEntity.toModel().calculateTimeAgo())
//                .postPhotoImgUrl(!postEntity.getPostPhotoEntityList().isEmpty() ?
//                        postEntity.getPostPhotoEntityList().get(0).getPhotoImgUrl() : null)
//                .postVisitCount(postEntity.getPostVisitCount())
//                .scrapCount(postEntity.getPostScrapList().size())
//                .build();
//    }

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
                .likeCount(post.getNetLikes())
                .likeOnlyCount(post.getLikeCount())
                .dislikeOnlyCount(post.getDislikeCount())
                .timeAgo(post.calculateTimeAgo())
                .postPhotoImgUrl(!post.getPhotos().isEmpty() ? post.getPhotos().get(0).getPhotoImgUrl() : null)
                .commentCount((int) post.getComments().stream()
                        .filter(c -> c.getStatus() == ContentStatus.ACTIVE)
                        .count())
                .postVisitCount(post.getVisitCount())
                .scrapCount(post.getScraps() == null ? 0 : post.getScraps().size())
                .build();
        if (author != null) {
            postDTO.setUser(UserDTO.from(author));
        }
        return postDTO;
    }
}
