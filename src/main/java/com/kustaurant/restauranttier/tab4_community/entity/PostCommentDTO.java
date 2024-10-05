package com.kustaurant.restauranttier.tab4_community.entity;

import com.kustaurant.restauranttier.tab4_community.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostCommentDTO {
    @Schema(description = "댓글 id", example = "100")
    Integer commentId;
    @Schema(description = "댓글 내용", example = "안녕하세요~")
    private String commentBody;
    @Schema(description = "댓글 상태 (활성화 or 삭제)", example = "ACTIVE")
    private String status;
    @Schema(description = "댓글 작성자", example = "임재")
    private UserDTO user;
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
    public PostCommentDTO(Integer commentId,String commentBody, UserDTO user, String status,Integer likeCount, Integer dislikeCount, String timeAgo, LocalDateTime createdAt, LocalDateTime updatedAt, List<PostCommentDTO> repliesList) {
        this.commentId= commentId;
        this.commentBody = commentBody;
        this.user = user;
        this.status =status;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.timeAgo = timeAgo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.repliesList = repliesList;
    }

    public static PostCommentDTO fromEntity(PostComment comment) {
        UserDTO userDTO = UserDTO.fromEntity(comment.user);
        return new PostCommentDTO(comment.commentId,comment.getCommentBody(), userDTO, comment.status, comment.getLikeCount(), comment.getDislikeUserList().size(),comment.calculateTimeAgo(),comment.createdAt, comment.updatedAt, comment.getRepliesList().stream().map(PostCommentDTO::fromEntity).toList());
    }

}
