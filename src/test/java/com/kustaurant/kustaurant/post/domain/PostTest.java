package com.kustaurant.kustaurant.post.domain;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {
    private Post post;

    @BeforeEach
    void 초기화() {
        // Given
        post = Post.builder()
                .id(1)
                .title("Original Title")
                .body("Original Body")
                .category("General")
                .status(ContentStatus.ACTIVE)
                .authorId(123L)
                .visitCount(0)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 좋아요를_처음_누르면_생성된다() {
        // When
        ReactionStatus result = post.toggleLike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_CREATED);
    }

    @Test
    void 좋아요를_이미_누른_상태에서_한번_더_누르면_취소된다() {
        // When
        ReactionStatus result = post.toggleLike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_DELETED);
    }

    @Test
    void 싫어요에서_좋아요로_변경하면_DISLIKE_TO_LIKE_상태가_반환된다() {
        // When
        ReactionStatus result = post.toggleLike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
    }

    @Test
    void 싫어요를_처음_누르면_생성된다() {
        // When
        ReactionStatus result = post.toggleDislike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_CREATED);
    }

    @Test
    void 싫어요를_이미_누른_상태에서_한번_더_누르면_취소된다() {
        // When
        ReactionStatus result = post.toggleDislike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_DELETED);
    }

    @Test
    void 좋아요에서_싫어요로_변경하면_LIKE_TO_DISLIKE_상태가_반환된다() {
        // When
        ReactionStatus result = post.toggleDislike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_TO_DISLIKE);
    }

    @Test
    void 게시글을_수정하면_제목_본문_카테고리가_변경된다() {
        // When
        post.update("New Title", "New Body", "Food", Arrays.asList("url1", "url2"));

        // Then
        assertThat(post.getTitle()).isEqualTo("New Title");
        assertThat(post.getBody()).isEqualTo("New Body");
        assertThat(post.getCategory()).isEqualTo("Food");
    }

    @Test
    void 게시글을_삭제하면_상태가_DELETED로_변경된다() {
        // When
        post.delete();

        // Then
        assertThat(post.getStatus()).isEqualTo(ContentStatus.DELETED);
    }

    @Test
    void 작성된지_1시간_이내면_분단위로_시간이_표시된다() {
        // When
        String result = post.calculateTimeAgo();

        // Then
        assertThat(result).contains("분 전");
    }

    @Test
    void 작성된지_2일_이상_지나면_일단위로_시간이_표시된다() {
        // Given
        post = Post.builder()
                .id(2)
                .title("Old Post")
                .body("Content")
                .category("Talk")
                .status(ContentStatus.ACTIVE)
                .authorId(456L)
                .visitCount(0)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        String result = post.calculateTimeAgo();

        // Then
        assertThat(result).isEqualTo("2일 전");
    }
    
    @Test
    void 포스트_빌더가_정상적으로_작동한다() {
        // Given & When
        Post testPost = Post.builder()
                .id(100)
                .title("Test Title")
                .body("Test Body")
                .category("Test Category")
                .status(ContentStatus.ACTIVE)
                .authorId(999L)
                .visitCount(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(testPost.getId()).isEqualTo(100);
        assertThat(testPost.getTitle()).isEqualTo("Test Title");
        assertThat(testPost.getBody()).isEqualTo("Test Body");
        assertThat(testPost.getCategory()).isEqualTo("Test Category");
        assertThat(testPost.getStatus()).isEqualTo(ContentStatus.ACTIVE);
        assertThat(testPost.getAuthorId()).isEqualTo(999L);
        assertThat(testPost.getVisitCount()).isEqualTo(5);
    }
}