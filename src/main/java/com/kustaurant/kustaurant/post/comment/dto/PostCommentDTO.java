package com.kustaurant.kustaurant.post.comment.dto;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.projection.PostCommentDetailProjection;
import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Builder.Default
    private Boolean isDisliked = false;
    @Schema(description = "좋아요 여부", example = "false")
    @Builder.Default
    private Boolean isLiked = false;
    @Schema(description = "나의 댓글인지의 여부", example = "false")
    @Builder.Default
    private Boolean isCommentMine = false;
    @Schema(description = "작성 유저")
    UserDTO user;

    public static PostCommentDTO from(PostComment comment, Map<Long, UserDTO> userDtoMap) {
        return PostCommentDTO.builder()
                .commentId(comment.getId())
                .commentBody(comment.getCommentBody())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(userDtoMap.get(comment.getUserId()))
                .likeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .dislikeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .timeAgo(TimeAgoUtil.toKor(comment.getCreatedAt()))
                .repliesList(List.of()) // ID 기반으로 별도 조회 필요
                .build();
    }

    public static PostCommentDTO from(PostComment comment, User user) {
        return PostCommentDTO.builder()
                .commentId(comment.getId())
                .commentBody(comment.getCommentBody())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(UserDTO.from(user))
                .likeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .dislikeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .timeAgo(TimeAgoUtil.toKor(comment.getCreatedAt()))
                .repliesList(List.of()) // ID 기반으로 별도 조회 필요
                .build();
    }

    public static PostCommentDTO from(PostCommentDetailProjection projection) {
        return PostCommentDTO.builder()
                .commentId(projection.commentId())
                .commentBody(projection.commentBody())
                .status(projection.status())
                .createdAt(projection.createdAt())
                .updatedAt(projection.updatedAt())
                .user(createUserDTO(projection))
                .likeCount(projection.getLikeOnlyCount())
                .dislikeCount(projection.getDislikeOnlyCount())
                .timeAgo(TimeAgoUtil.toKor(projection.createdAt()))
                .isLiked(projection.isLiked())
                .isDisliked(projection.isDisliked())
                .isCommentMine(projection.isMine())
                .repliesList(List.of()) // 대댓글은 별도로 처리
                .build();
    }

    private static UserDTO createUserDTO(PostCommentDetailProjection projection) {
        UserDTO userDTO = new UserDTO();
        userDTO.setNickname(projection.userNickname());
        userDTO.setRankImg(UserIconResolver.resolve(projection.userEvaluationCount()));
        userDTO.setEvaluationCount(projection.userEvaluationCount());
        return userDTO;
    }
}
