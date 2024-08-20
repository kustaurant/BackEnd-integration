package com.kustaurant.restauranttier.tab4_community.entity;

import com.kustaurant.restauranttier.tab4_community.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentDTO {
    private String commentBody;
    private UserDTO user;
    private Integer likeCount;
    private Integer dislikeCount;

    public PostCommentDTO(String commentBody, UserDTO user, Integer likeCount, Integer dislikeCount) {
        this.commentBody = commentBody;
        this.user = user;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public static PostCommentDTO fromEntity(PostComment comment) {
        UserDTO userDTO = UserDTO.fromEntity(comment.user);
        return new PostCommentDTO(comment.getCommentBody(), userDTO, comment.getLikeCount(), comment.getDislikeUserList().size());
    }}
