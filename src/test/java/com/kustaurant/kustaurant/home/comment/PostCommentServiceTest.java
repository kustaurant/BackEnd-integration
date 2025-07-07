package com.kustaurant.kustaurant.home.comment;

import com.kustaurant.kustaurant.post.comment.controller.web.PostCommentService;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentDislikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentLikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.service.web.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostCommentServiceTest {
    private PostCommentRepository commentRepository;
    private PostCommentLikeJpaRepository likeRepository;
    private PostCommentDislikeJpaRepository dislikeRepository;
    private PostService postService;
    private UserRepository userRepository;
    private PostCommentService commentService;
    private UserService userService;
    private PostCommentLikeRepository postCommentLikeRepository;
    private PostCommentDislikeRepository postCommentDislikeRepository;
    @BeforeEach
    void setUp() {
        commentRepository = mock(PostCommentRepository.class);
        likeRepository = mock(PostCommentLikeJpaRepository.class);
        dislikeRepository = mock(PostCommentDislikeJpaRepository.class);
        postService = mock(PostService.class);
        userRepository = mock(UserRepository.class);
        postCommentDislikeRepository = mock(PostCommentDislikeRepository.class);
        postCommentLikeRepository = mock(PostCommentLikeRepository.class);

        commentService = new PostCommentService(commentRepository, postService, likeRepository, dislikeRepository,userService, postCommentLikeRepository, postCommentDislikeRepository);
    }

    @Test
    void 댓글을_조회할_수_있다() {
        // Given
        Integer commentId = 1;
        PostComment mockComment = PostComment.create("content", 100L, 1);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        // When
        PostComment result = commentService.getPostCommentByCommentId(commentId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCommentBody()).isEqualTo("content");
    }

    @Test
    void 존재하지_않는_댓글_조회시_예외를_던진다() {
        // Given
        Integer commentId = 1;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.getPostCommentByCommentId(commentId))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage("PostComment not found");
    }

    @Test
    void 댓글_좋아요_처음_누르면_좋아요가_생성된다() {
        // Given
        Long userId = 10L;
        Integer commentId = 1;
        PostComment comment = PostComment.create("like test", userId, 2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(dislikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);

        // When
        ReactionToggleResponse response = commentService.toggleLike(userId, commentId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.LIKE_CREATED);
        assertThat(response.getLikeCount()).isEqualTo(1);
        verify(commentRepository).save(comment);
    }

    @Test
    void 댓글_싫어요에서_좋아요로_전환시_싫어요_감소_좋아요_증가한다() {
        // Given
        Long userId = 10L;
        Integer commentId = 1;
        PostComment comment = PostComment.create("switch from dislike", userId, 2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(dislikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);

        // When
        ReactionToggleResponse response = commentService.toggleLike(userId, commentId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getDislikeCount()).isEqualTo(0);
    }

    @Test
    void 댓글을_삭제하면_상태가_DELETED로_변경된다() {
        // Given
        Integer commentId = 1;
        PostComment reply = PostComment.create("reply", 100L, 1);
        PostComment parentComment = PostComment.create("parent", 100L, 1);
        when(commentRepository.findByIdWithReplies(commentId)).thenReturn(Optional.of(parentComment));

        // When
        int deletedCount = commentService.deleteComment(commentId);

        // Then
        assertThat(deletedCount).isEqualTo(2);
        verify(commentRepository).save(parentComment);
    }

    @Test
    void 댓글목록을_조회할_때_인기순이면_좋아요순으로_정렬된다() {
        // Given
        Integer postId = 1;
        Post post = Post.builder().id(postId).build();
        PostComment comment1 = PostComment.create("1", 1L, postId);
        PostComment comment2 = PostComment.create("2", 1L, postId);
        when(postService.getPost(postId)).thenReturn(post);
        when(commentRepository.findParentComments(any())).thenReturn(new ArrayList<>(List.of(comment1, comment2)));

        // When
        List<PostComment> result = commentService.getParentComments(postId, "popular");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCommentBody()).isEqualTo("1");
        assertThat(result.get(1).getCommentBody()).isEqualTo("2");
    }

    @Test
    void 댓글목록을_조회할_때_최신순이면_작성시간순으로_정렬된다() {
        // Given
        Integer postId = 1;
        Post post = Post.builder().id(postId).build();
        PostComment older = PostComment.create("older", 1L, postId);
        PostComment newer = PostComment.create("newer", 1L, postId);
        older.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        newer.setCreatedAt(LocalDateTime.now());
        when(postService.getPost(postId)).thenReturn(post);
        when(commentRepository.findParentComments(any())).thenReturn(new ArrayList<>(List.of(older, newer)));

        // When
        List<PostComment> result = commentService.getParentComments(postId, "recent");

        // Then
        assertThat(result.get(0).getCreatedAt()).isAfter(result.get(1).getCreatedAt());
    }
}
