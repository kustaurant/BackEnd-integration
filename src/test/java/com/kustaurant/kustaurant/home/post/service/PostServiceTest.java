package com.kustaurant.kustaurant.home.post.service;

import com.kustaurant.kustaurant.post.post.domain.ImageExtractor;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.service.port.*;
import com.kustaurant.kustaurant.post.post.service.web.PostService;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostScrapRepository postScrapRepository;
    private PostLikeRepository postLikeRepository;
    private PostDislikeRepository postDislikeRepository;
    private PostPhotoRepository postPhotoRepository;
    private ImageExtractor imageExtractor;
    private UserService userService;
    private PostService postService;

    @BeforeEach
    void 설정() {
        postRepository = mock(PostRepository.class);
        userRepository = mock(UserRepository.class);
        postScrapRepository = mock(PostScrapRepository.class);
        postLikeRepository = mock(PostLikeRepository.class);
        postDislikeRepository = mock(PostDislikeRepository.class);
        postPhotoRepository = mock(PostPhotoRepository.class);
        imageExtractor = mock(ImageExtractor.class);
        userService = mock(UserService.class);

        PostQueryDAO postQueryDAO = mock(PostQueryDAO.class);
        postService = new PostService(postRepository, userRepository,
                postScrapRepository, postLikeRepository, postDislikeRepository,
                postPhotoRepository, imageExtractor, postQueryDAO, userService);
    }

    @Test
    void 좋아요를_처음_누르면_좋아요가_생성된다() {
        // Given
        int postId = 1;
        Long userId = 10L;

        Post post = Post.builder()
                .id(postId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .authorId(userId)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);
        when(postDislikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);

        // When
        ReactionToggleResponse response = postService.toggleLike(postId, userId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.LIKE_CREATED);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getDislikeCount()).isEqualTo(0);
        verify(postLikeRepository).save(any());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void 싫어요가_눌린_상태에서_좋아요를_누르면_싫어요는_삭제되고_좋아요가_생성된다() {
        // Given
        int postId = 1;
        Long userId = 20L;

        Post post = Post.builder()
                .id(postId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .authorId(userId)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
        when(postLikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(false);
        when(postDislikeRepository.existsByUserIdAndPostId(userId, postId)).thenReturn(true);

        // When
        ReactionToggleResponse response = postService.toggleLike(postId, userId);

        // Then
        assertThat(response.getStatus()).isEqualTo(ReactionStatus.DISLIKE_TO_LIKE);
        assertThat(response.getLikeCount()).isEqualTo(1);
        assertThat(response.getDislikeCount()).isEqualTo(0);
        verify(postDislikeRepository).deleteByUserIdAndPostId(userId, postId);
        verify(postLikeRepository).save(any());
        verify(postRepository).save(post);
    }

    @Test
    void 존재하지_않는_게시글을_조회하면_예외가_발생한다() {
        // Given
        int invalidPostId = 999;
        when(postRepository.findById(invalidPostId)).thenReturn(Optional.empty());

        // When & Then
        try {
            postService.getPost(invalidPostId);
        } catch (DataNotFoundException e) {
            assertThat(e.getMessage()).isEqualTo("post not found");
        }
    }

    @Test
    void 게시글_생성시_본문에서_이미지_추출하여_사진이_저장된다() {
        // Given
        String title = "제목";
        String category = "일반";
        String body = "<img src='img1.jpg'>본문";
        Long userId = 1L;

        when(imageExtractor.extract(body)).thenReturn(java.util.List.of("img1.jpg"));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        ArgumentCaptor<PostPhoto> photoCaptor = ArgumentCaptor.forClass(PostPhoto.class);

        // When
        postService.create(title, category, body, userId);

        // Then
        verify(postRepository).save(postCaptor.capture());
        verify(postPhotoRepository).save(photoCaptor.capture());

        assertThat(postCaptor.getValue().getTitle()).isEqualTo(title);
        assertThat(photoCaptor.getValue().getPhotoImgUrl()).isEqualTo("img1.jpg");
    }
}