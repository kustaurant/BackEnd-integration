package com.kustaurant.kustaurant.common.comment.domain;

import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PostCommentTest {

    @Test
    void 댓글_생성시_기본값이_잘_설정된다() {
        // Given
        String content = "Test comment";
        Integer userId = 1;
        Integer postId = 10;

        // When
        PostComment comment = PostComment.create(content, userId, postId);

        // Then
        assertThat(comment.getCommentBody()).isEqualTo(content);
        assertThat(comment.getUserId()).isEqualTo(userId);
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getStatus()).isEqualTo(ContentStatus.ACTIVE);
        assertThat(comment.getLikeCount()).isEqualTo(0);
        assertThat(comment.getDislikeCount()).isEqualTo(0);
        assertThat(comment.getNetLikes()).isEqualTo(0);
        assertThat(comment.getReplies()).isEmpty();
        assertThat(comment.getCreatedAt()).isNotNull();
    }

    @Test
    void 댓글_좋아요_처음_누르면_좋아요가_증가한다() {
        // Given
        PostComment comment = PostComment.create("Like test", 1, 1);

        // When
        ReactionStatus result = comment.toggleLike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_CREATED);
        assertThat(comment.getLikeCount()).isEqualTo(1);
        assertThat(comment.getDislikeCount()).isEqualTo(0);
        assertThat(comment.getNetLikes()).isEqualTo(1);
    }

    @Test
    void 댓글_좋아요_취소하면_좋아요가_감소한다() {
        // Given
        PostComment comment = PostComment.create("Like cancel", 1, 1);
        comment.increaseLikeCount(1);

        // When
        ReactionStatus result = comment.toggleLike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_DELETED);
        assertThat(comment.getLikeCount()).isEqualTo(0);
        assertThat(comment.getNetLikes()).isEqualTo(0);
    }

    @Test
    void 댓글_싫어요에서_좋아요로_전환하면_싫어요가_감소하고_좋아요가_증가한다() {
        // Given
        PostComment comment = PostComment.create("Switch", 1, 1);
        comment.increaseDislikeCount(1);

        // When
        ReactionStatus result = comment.toggleLike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
        assertThat(comment.getLikeCount()).isEqualTo(1);
        assertThat(comment.getDislikeCount()).isEqualTo(0);
        assertThat(comment.getNetLikes()).isEqualTo(1);
    }

    @Test
    void 댓글을_삭제하면_상태가_DELETED로_변경된다() {
        // Given
        PostComment parentComment = PostComment.create("Parent comment", 1, 1);
        PostComment reply = PostComment.create("Reply", 1, 1);
        parentComment.getReplies().add(reply);

        // When
        parentComment.delete();

        // Then
        assertThat(parentComment.getStatus()).isEqualTo(ContentStatus.DELETED);
        assertThat(parentComment.getReplies().get(0).getStatus()).isEqualTo(ContentStatus.DELETED);
    }

    @Test
    void 시간차이를_분단위로_계산할_수_있다() {
        // Given
        PostComment comment = PostComment.create("Comment", 1, 1);
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(10));

        // When
        String timeAgo = comment.calculateTimeAgo();

        // Then
        assertThat(timeAgo).isEqualTo("10분 전");
    }

    @Test
    void 시간차이를_초단위로_계산할_수_있다() {
        // Given
        PostComment comment = PostComment.create("Comment", 1, 1);
        comment.setCreatedAt(LocalDateTime.now().minusSeconds(30));

        // When
        String timeAgo = comment.calculateTimeAgo();

        // Then
        assertThat(timeAgo).isEqualTo("30초 전");
    }
}
