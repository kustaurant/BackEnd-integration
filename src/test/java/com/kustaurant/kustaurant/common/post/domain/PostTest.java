package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
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
                .authorId(123)
                .likeCount(0)
                .dislikeCount(0)
                .netLikes(0)
                .visitCount(0)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now())
                .photos(new java.util.ArrayList<>())
                .scraps(new java.util.ArrayList<>())
                .comments(new java.util.ArrayList<>())
                .build();
    }

    @Test
    void 좋아요를_처음_누르면_생성된다() {
        // When
        ReactionStatus result = post.toggleLike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_CREATED);
        assertThat(post.getLikeCount()).isEqualTo(1);
        assertThat(post.getNetLikes()).isEqualTo(1);
    }

    @Test
    void 좋아요를_이미_누른_상태에서_한번_더_누르면_취소된다() {
        // Given
        post.increaseLikeCount(1);

        // When
        ReactionStatus result = post.toggleLike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_DELETED);
        assertThat(post.getLikeCount()).isEqualTo(0);
        assertThat(post.getNetLikes()).isEqualTo(0);
    }

    @Test
    void 싫어요에서_좋아요로_변경하면_싫어요는_감소하고_좋아요는_증가한다() {
        // Given
        post.increaseDislikeCount(1);

        // When
        ReactionStatus result = post.toggleLike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
        assertThat(post.getLikeCount()).isEqualTo(1);
        assertThat(post.getDislikeCount()).isEqualTo(0);
        assertThat(post.getNetLikes()).isEqualTo(1);
    }

    @Test
    void 싫어요를_처음_누르면_생성된다() {
        // When
        ReactionStatus result = post.toggleDislike(false, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_CREATED);
        assertThat(post.getDislikeCount()).isEqualTo(1);
        assertThat(post.getNetLikes()).isEqualTo(-1);
    }

    @Test
    void 싫어요를_이미_누른_상태에서_한번_더_누르면_취소된다() {
        // Given
        post.increaseDislikeCount(1);

        // When
        ReactionStatus result = post.toggleDislike(false, true);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.DISLIKE_DELETED);
        assertThat(post.getDislikeCount()).isEqualTo(0);
        assertThat(post.getNetLikes()).isEqualTo(0);
    }

    @Test
    void 좋아요에서_싫어요로_변경하면_좋아요는_감소하고_싫어요는_증가한다() {
        // Given
        post.increaseLikeCount(1);

        // When
        ReactionStatus result = post.toggleDislike(true, false);

        // Then
        assertThat(result).isEqualTo(ReactionStatus.LIKE_TO_DISLIKE);
        assertThat(post.getLikeCount()).isEqualTo(0);
        assertThat(post.getDislikeCount()).isEqualTo(1);
        assertThat(post.getNetLikes()).isEqualTo(-1);
    }

    @Test
    void 게시글을_수정하면_제목_본문_카테고리_이미지가_변경된다() {
        // When
        post.update("New Title", "New Body", "Food", Arrays.asList("url1", "url2"));

        // Then
        assertThat(post.getTitle()).isEqualTo("New Title");
        assertThat(post.getBody()).isEqualTo("New Body");
        assertThat(post.getCategory()).isEqualTo("Food");
        assertThat(post.getPhotos()).hasSize(2);
        assertThat(post.getPhotos().get(0).getPhotoImgUrl()).isEqualTo("url1");
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
                .authorId(456)
                .likeCount(0)
                .dislikeCount(0)
                .netLikes(0)
                .visitCount(0)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now())
                .photos(new java.util.ArrayList<>())
                .scraps(new java.util.ArrayList<>())
                .comments(new java.util.ArrayList<>())
                .build();

        // When
        String result = post.calculateTimeAgo();

        // Then
        assertThat(result).isEqualTo("2일 전");
    }
}
