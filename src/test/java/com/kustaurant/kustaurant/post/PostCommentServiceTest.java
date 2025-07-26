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
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.LIKE_CREATED);
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

    // 댓글 목록 조회 테스트는 제거된 getParentComments 메서드 대신
    // buildPostDetailView 메서드나 PostCommentQueryDAO를 직접 테스트하는 것이 좋습니다.
    // 실제 통합 테스트에서 전체 플로우를 검증하는 것을 권장합니다.
}
