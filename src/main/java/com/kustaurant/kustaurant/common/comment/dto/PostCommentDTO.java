package com.kustaurant.kustaurant.common.comment.dto;

import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.post.domain.UserDTO;
import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class PostCommentDTO {
    @Schema(description = "댓글 id", example = "100")
    Integer commentId;
    @Schema(description = "댓글 내용", example = "안녕하세요~")
    private String commentBody;
    @Schema(description = "댓글 상태 (활성화 or 삭제)", example = "ACTIVE")
    private ContentStatus status;
    @Schema(description = "좋아요 수", example = "3")
    private Integer likeCount;
    @Schema(description = "싫어요 수", example = "2")
    private Integer dislikeCount;
    @Schema(description = "댓글이 작성된 날짜", example = "2024-05-19T18:09:06")
    private LocalDateTime createdAt;
    @Schema(description = "댓글이 업데이트된 날짜", example = "2024-05-19T18:09:06")
    private LocalDateTime updatedAt;
    @Schema(description = "댓글에 달린 대댓글 리스트")
    private List<PostCommentDTO> repliesList;
    @Schema(description = "시간 경과", example = "8일 전")
    private String timeAgo;
    @Schema(description = "싫어요 여부", example = "true")
    private Boolean isDisliked = false;
    @Schema(description = "좋아요 여부", example = "false")
    private Boolean isLiked = false;
    @Schema(description = "나의 댓글인지의 여부", example = "false")
    private Boolean isCommentMine = false;
    @Schema(description = "작성 유저")
    UserDTO user;
    public PostCommentDTO(Integer commentId, String commentBody, ContentStatus status, UserEntity UserEntity, Integer likeCount, Integer dislikeCount, String timeAgo, LocalDateTime createdAt, LocalDateTime updatedAt, List<PostCommentDTO> repliesList) {
        this.commentId = commentId;
        this.commentBody = commentBody;
        this.status = status;
        this.user = UserDTO.convertUserToUserDTO(UserEntity);
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.timeAgo = timeAgo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.repliesList = repliesList;
    }

    public static PostCommentDTO convertPostCommentToPostCommentDTO(PostCommentEntity comment) {
        return new PostCommentDTO(comment.getCommentId(), comment.getCommentBody(),comment.getStatus(),comment.getUser(), comment.getPostCommentLikesEntities().size(), comment.getPostCommentDislikesEntities().size(), comment.calculateTimeAgo(), comment.getCreatedAt(), comment.getUpdatedAt(), comment.getRepliesList().stream().filter(reply -> reply.getStatus().equals("ACTIVE")).sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed()).map(PostCommentDTO::convertPostCommentToPostCommentDTO).toList());
    }

}
