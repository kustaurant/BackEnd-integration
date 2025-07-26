package com.kustaurant.kustaurant.post;

import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentDislikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentLikeJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryDAO;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.service.web.PostQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostCommentServiceTest {
    private PostCommentRepository commentRepository;
    private PostCommentQueryDAO postCommentQueryDAO;
    private PostQueryDAO postQueryDAO;
    private PostCommentLikeJpaRepository likeRepository;
    private PostCommentDislikeJpaRepository dislikeRepository;
    private PostQueryService postQueryService;
    private UserRepository userRepository;
    private PostCommentService commentService;
    private UserService userService;
    private PostCommentLikeRepository postCommentLikeRepository;
    private PostCommentDislikeRepository postCommentDislikeRepository;
    
    @BeforeEach
    void setUp() {
        commentRepository = mock(PostCommentRepository.class);
        postCommentQueryDAO = mock(PostCommentQueryDAO.class);
        postQueryDAO = mock(PostQueryDAO.class);
        likeRepository = mock(PostCommentLikeJpaRepository.class);
        dislikeRepository = mock(PostCommentDislikeJpaRepository.class);
        postQueryService = mock(PostQueryService.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        postCommentDislikeRepository = mock(PostCommentDislikeRepository.class);
        postCommentLikeRepository = mock(PostCommentLikeRepository.class);

        commentService = new PostCommentService(commentRepository, postCommentQueryDAO, postQueryDAO, postQueryService, likeRepository, dislikeRepository, userService, postCommentLikeRepository, postCommentDislikeRepository);
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
                .hasMessage("ID가 1인 댓글이(가) 존재하지 않습니다.");
    }

    @Test
    void 댓글_좋아요_처음_누르면_좋아요가_생성된다() {
        // Given
        Long userId = 10L;
        Integer commentId = 1;
        PostComment comment = PostComment.create("like test", userId, 2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(postCommentDislikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(postCommentLikeRepository.countByCommentId(commentId)).thenReturn(1);
        when(postCommentDislikeRepository.countByCommentId(commentId)).thenReturn(0);

        // When
        ReactionToggleResponse response = commentService.toggleLike(userId, commentId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.LIKE_CREATED);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getDislikeCount()).isEqualTo(0);
        verify(postCommentLikeRepository).save(any());
    }

    @Test
    void 댓글_싫어요에서_좋아요로_전환시_싫어요_감소_좋아요_증가한다() {
        // Given
        Long userId = 10L;
        Integer commentId = 1;
        PostComment comment = PostComment.create("switch from dislike", userId, 2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(false);
        when(postCommentDislikeRepository.existsByUserIdAndCommentId(userId, commentId)).thenReturn(true);
        when(postCommentLikeRepository.countByCommentId(commentId)).thenReturn(1);
        when(postCommentDislikeRepository.countByCommentId(commentId)).thenReturn(0);

        // When
        ReactionToggleResponse response = commentService.toggleLike(userId, commentId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getDislikeCount()).isEqualTo(0);
        verify(postCommentDislikeRepository).deleteByUserIdAndCommentId(userId, commentId);
        verify(postCommentLikeRepository).save(any());
    }

    @Test
    void 댓글을_삭제하면_상태가_DELETED로_변경된다() {
        // Given
        Integer commentId = 1;
        PostComment parentComment = PostComment.create("parent", 100L, 1);
        PostComment reply1 = PostComment.create("reply1", 101L, 1);
        PostComment reply2 = PostComment.create("reply2", 102L, 1);
        List<PostComment> replies = List.of(reply1, reply2);
        
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(parentComment));
        when(commentRepository.findByParentCommentId(commentId)).thenReturn(replies);
        when(commentRepository.saveAll(replies)).thenReturn(replies);

        // When
        int deletedCount = commentService.deleteComment(commentId);

        // Then
        assertThat(deletedCount).isEqualTo(3);
        verify(commentRepository).save(parentComment);
        verify(commentRepository).saveAll(replies);
    }

}
