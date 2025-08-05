package com.kustaurant.kustaurant.post.domain;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.domain.enums.depricated.ReactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
                .category(PostCategory.FREE)
                .status(PostStatus.ACTIVE)
                .writerId(123L)
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

//    @Test
//    void 게시글을_수정하면_제목_본문_카테고리가_변경된다() {
//        // When
//        post.update("New Title", "New Body", PostCategory.COLUMN, Arrays.asList("url1", "url2"));
//
//        // Then
//        assertThat(post.getTitle()).isEqualTo("New Title");
//        assertThat(post.getBody()).isEqualTo("New Body");
//        assertThat(post.getCategory()).isEqualTo(PostCategory.COLUMN);
//    }
//
//    @Test
//    void 게시글을_삭제하면_상태가_DELETED로_변경된다() {
//        // When
//        post.delete();
//
//        // Then
//        assertThat(post.getStatus()).isEqualTo(PostStatus.DELETED);
//    }
    
    @Test
    void 포스트_빌더가_정상적으로_작동한다() {
        // Given & When
        Post testPost = Post.builder()
                .id(100)
                .title("Test Title")
                .body("Test Body")
                .category(PostCategory.COLUMN)
                .status(PostStatus.ACTIVE)
                .writerId(999L)
                .visitCount(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertThat(testPost.getId()).isEqualTo(100);
        assertThat(testPost.getTitle()).isEqualTo("Test Title");
        assertThat(testPost.getBody()).isEqualTo("Test Body");
        assertThat(testPost.getCategory()).isEqualTo(PostCategory.COLUMN);
        assertThat(testPost.getStatus()).isEqualTo(PostStatus.ACTIVE);
        assertThat(testPost.getWriterId()).isEqualTo(999L);
        assertThat(testPost.getVisitCount()).isEqualTo(5);
    }
}