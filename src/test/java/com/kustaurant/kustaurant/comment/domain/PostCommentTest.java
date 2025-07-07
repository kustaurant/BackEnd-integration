package com.kustaurant.kustaurant.comment.domain;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostCommentTest {

    @Test
    void 댓글_생성시_기본값이_잘_설정된다() {
        // Given
        String content = "Test comment";
        Long userId = 1L;
        Integer postId = 10;

        // When
        PostComment comment = PostComment.create(content, userId, postId);

        // Then
        assertThat(comment.getCommentBody()).isEqualTo(content);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getStatus()).isEqualTo(ContentStatus.ACTIVE);
        assertThat(comment.getReplyIds()).isEmpty();
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    void 댓글_좋아요_처음_누르면_LIKE_CREATED_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Like test", 1L, 1);

        // When
        ReactionStatus result = comment.toggleLike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_CREATED);
    }

    @Test
    void 댓글_좋아요_취소하면_LIKE_DELETED_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Like cancel", 1L, 1);

        // When
        ReactionStatus result = comment.toggleLike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_DELETED);
    }

    @Test
    void 댓글_싫어요에서_좋아요로_전환하면_DISLIKE_TO_LIKE_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Switch", 1L, 1);

        // When
        ReactionStatus result = comment.toggleLike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
    }

    @Test
    void 댓글_싫어요_처음_누르면_DISLIKE_CREATED_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Dislike test", 1L, 1);

        // When
        ReactionStatus result = comment.toggleDislike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_CREATED);
    }

    @Test
    void 댓글_싫어요_취소하면_DISLIKE_DELETED_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Dislike cancel", 1L, 1);

        // When
        ReactionStatus result = comment.toggleDislike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_DELETED);
    }

    @Test
    void 댓글_좋아요에서_싫어요로_전환하면_LIKE_TO_DISLIKE_상태가_반환된다() {
        // Given
        PostComment comment = PostComment.create("Switch to dislike", 1L, 1);

        // When
        ReactionStatus result = comment.toggleDislike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_TO_DISLIKE);
    }

    @Test
    void 댓글을_삭제하면_상태가_DELETED로_변경된다() {
        // Given
        PostComment comment = PostComment.create("Comment to delete", 1L, 1);

        // When
        comment.delete();

        // Then
        assertThat(comment.getStatus()).isEqualTo(ContentStatus.DELETED);
    }

    @Test
    void 시간차이를_분단위로_계산할_수_있다() {
        // Given
        PostComment comment = PostComment.create("Comment", 1L, 1);
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(10));

        // When
        String timeAgo = comment.calculateTimeAgo();

        // Then
        assertThat(timeAgo).isEqualTo("10분 전");
    }

    @Test
    void 시간차이를_초단위로_계산할_수_있다() {
        // Given
        PostComment comment = PostComment.create("Comment", 1L, 1);
        comment.setCreatedAt(LocalDateTime.now().minusSeconds(30));

        // When
        String timeAgo = comment.calculateTimeAgo();

        // Then
        assertThat(timeAgo).isEqualTo("30초 전");
    }

    @Test
    void 부모_댓글_ID를_설정할_수_있다() {
        // Given
        PostComment comment = PostComment.create("Reply comment", 1L, 1);

        // When
        comment.setParentCommentId(5);

        // Then
        assertThat(comment.getParentCommentId()).isEqualTo(5);
    }

    @Test
    void 댓글_빌더가_정상적으로_작동한다() {
        // Given & When
        LocalDateTime now = LocalDateTime.now();
        PostComment comment = PostComment.builder()
                .id(100)
                .commentBody("Test comment body")
                .status(ContentStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .userId(999L)
                .postId(555)
                .build();

        // Then
        assertThat(comment.getId()).isEqualTo(100);
        assertThat(comment.getCommentBody()).isEqualTo("Test comment body");
        assertThat(comment.getStatus()).isEqualTo(ContentStatus.ACTIVE);
        assertThat(comment.getUserId()).isEqualTo(999L);
        assertThat(comment.getPostId()).isEqualTo(555);
        assertThat(comment.getCreatedAt()).isEqualTo(now);
        assertThat(comment.getUpdatedAt()).isEqualTo(now);
    }
}