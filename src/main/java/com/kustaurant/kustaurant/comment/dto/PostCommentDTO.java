package com.kustaurant.kustaurant.comment.dto;

import com.kustaurant.kustaurant.comment.domain.PostComment;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.domain.UserDTO;
import com.kustaurant.kustaurant.post.enums.ContentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Builder
@Slf4j
public class PostCommentDTO {
    @Schema(description = "댓글 id", example = "100")
    Integer commentId;
    @Schema(description = "댓글 내용", example = "안녕하세요~")
    private String commentBody;
    @Schema(description = "댓글 상태 (활성화 or 삭제)", example = "ACTIVE")
    private String status;
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

    public static PostCommentDTO convertPostCommentToPostCommentDTO(
            PostCommentEntity comment
    ) {
        String timeAgo = TimeAgoUtil.toKor(
                comment.getUpdatedAt() != null ? comment.getUpdatedAt()
                        : comment.getCreatedAt()
        );

        return PostCommentDTO.builder()
                .commentId(comment.getCommentId())
                .commentBody(comment.getCommentBody())
                .status(comment.getStatus().name())
                .user(UserDTO.convertUserToUserDTO(comment.getUser()))
                .likeCount(comment.getPostCommentLikesEntities().size())
                .dislikeCount(comment.getPostCommentDislikesEntities().size())
                .timeAgo(timeAgo)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .repliesList(
                        comment.getRepliesList().stream()
                                .filter(reply -> reply.getStatus().equals(ContentStatus.ACTIVE))
                                .sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed())
                                .map(PostCommentDTO::convertPostCommentToPostCommentDTO)
                                .toList()
                )
                .build();
    }


    public static PostCommentDTO from(PostComment comment, Map<Long, UserDTO> userDtoMap) {
        return PostCommentDTO.builder()
                .commentId(comment.getCommentId())
                .commentBody(comment.getCommentBody())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(userDtoMap.get(comment.getUserId()))
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .timeAgo(comment.calculateTimeAgo())
                .repliesList(
                        Optional.ofNullable(comment.getReplies())
                                .orElse(List.of())
                                .stream()
                                .filter(reply -> reply.getStatus() == ContentStatus.ACTIVE)
                                .map(reply -> PostCommentDTO.from(reply, userDtoMap))
                                .toList()
                )
                .build();
    }
}
