package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="post_comments_tbl")
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;
    String commentBody;

    @Column(columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    ContentStatus status;

    @ManyToOne
    @JoinColumn(name="parent_comment_id")
    PostCommentEntity parentComment;

    @OneToMany(mappedBy = "parentComment")
    List<PostCommentEntity> repliesList = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer likeCount=0; // 이 likeCount는 좋아요 수에서 싫어요 수를 뺀 순 좋아요 수를 의미

    public PostCommentEntity(String commentBody, ContentStatus status, LocalDateTime createdAt, PostEntity post, UserEntity UserEntity) {
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
        this.post = post;
        this.user = UserEntity;
    }

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity post;
    @ManyToOne
    @JoinColumn(name="user_id")
    UserEntity user;

    public PostCommentEntity() {

    }

    @OneToMany(mappedBy = "postComment")
    List<PostCommentLikeEntity> postCommentLikesEntities = new ArrayList<>();
    @OneToMany(mappedBy = "postComment")
    List<PostCommentDislikeEntity> postCommentDislikesEntities = new ArrayList<>();

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();

        // 연 차이 계산
        Long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference + "년 전";

        // 월 차이 계산
        Long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference + "달 전";

        // 일 차이 계산
        Long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference + "일 전";

        // 시간 차이 계산
        Long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference + "시간 전";

        // 분 차이 계산
        Long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference + "분 전";

        // 초 차이 계산
        Long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference + "초 전";
    }

    public static PostCommentEntity from(PostComment comment) {
        PostCommentEntity entity = new PostCommentEntity();
        entity.setCommentId(comment.getCommentId());
        entity.setCommentBody(comment.getCommentBody());
        entity.setStatus(comment.getStatus());
        entity.setLikeCount(comment.getNetLikes());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getUserId() != null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUserId(comment.getUserId());
            entity.setUser(userEntity);
        }

        // postId 처리
        if (comment.getPostId() != null) {
            PostEntity postEntity = new PostEntity();
            postEntity.setPostId(comment.getPostId());
            entity.setPost(postEntity);
        }

        // parentCommentId 처리
        if (comment.getParentComment() != null) {
            PostCommentEntity parent = new PostCommentEntity();
            parent.setCommentId(comment.getParentComment().getCommentId());
            entity.setParentComment(parent);
        }

        return entity;
    }

    public PostComment toDomain(boolean includeParent, boolean includeReplies) {
        PostComment.PostCommentBuilder builder = PostComment.builder()
                .commentId(this.commentId)
                .commentBody(this.commentBody)
                .postId(this.post.getPostId())
                .userId(this.user.getUserId())
                .netLikes(this.likeCount)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .likeCount(this.postCommentLikesEntities.size())
                .dislikeCount(this.postCommentDislikesEntities.size());

        if (includeParent && this.parentComment != null) {
            builder.parentComment(this.parentComment.toDomain(false, false)); // 깊이 제한
        }

        if (includeReplies && this.repliesList != null && !this.repliesList.isEmpty()) {
            List<PostComment> replies = new ArrayList<>();
            for (PostCommentEntity replyEntity : this.repliesList) {
                replies.add(replyEntity.toDomain(false, false)); // 자식도 깊이 제한
            }
            builder.replies(replies);
        } else {
            builder.replies(new ArrayList<>());
        }

        return builder.build();
    }

    public PostComment toDomain() {
        return toDomain(true, true);
    }
}
