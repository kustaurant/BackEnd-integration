package com.kustaurant.kustaurant.v1.community.dto;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Data
public class PostCommentDTO {
    Integer commentId;
    private String commentBody;
    private String status;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostCommentDTO> repliesList;
    private String timeAgo;
    private Boolean isDisliked = false;
    private Boolean isLiked = false;
    private Boolean isCommentMine = false;
    UserDTO user;
    public PostCommentDTO(Integer commentId, String commentBody, String status, User user, Integer likeCount, Integer dislikeCount, String timeAgo, LocalDateTime createdAt, LocalDateTime updatedAt, List<PostCommentDTO> repliesList) {
        this.commentId = commentId;
        this.commentBody = commentBody;
        this.status = status;
        this.user = UserDTO.convertUserToUserDTO(user);
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.timeAgo = timeAgo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.repliesList = repliesList;
    }

    public static PostCommentDTO convertPostCommentToPostCommentDTO(PostComment comment) {
        return new PostCommentDTO(
                comment.getCommentId(),
                comment.getCommentBody(),
                comment.getStatus(),comment.getUser(),
                comment.getLikeUserList().size(),
                comment.getDislikeUserList().size(),
                comment.calculateTimeAgo(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getRepliesList().stream()
                        .filter(reply -> reply.getStatus().equals("ACTIVE"))
                        .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed())
                        .map(PostCommentDTO::convertPostCommentToPostCommentDTO)
                        .toList()
        );
    }

}
