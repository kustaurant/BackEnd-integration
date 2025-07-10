package com.kustaurant.kustaurant.post.post.service.api;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.dto.PostUpdateDTO;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.web.PostCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostCommandApiService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final PostCommandService postCommandService;

    @Transactional
    public ReactionToggleResponse toggleLike(Integer postId, Long userId) {
        return postCommandService.toggleLike(postId, userId);
    }

    private void deleteComments(Post post) {
        Integer postId = post.getId();
        List<PostComment> comments = postCommentRepository.findByPostId(postId);
        comments.forEach(comment -> comment.setStatus(ContentStatus.DELETED));
        postCommentRepository.saveAll(comments);
    }

    private void deletePhotos(Post post) {
        Integer postId = post.getId();
        List<PostPhoto> photos = postPhotoRepository.findByPostId(postId);
        if (photos != null && !photos.isEmpty()) {
            for (PostPhoto photo : photos) {
                photo.setStatus(ContentStatus.DELETED);
            }
            postPhotoRepository.saveAll(photos);
        }
    }

    public void updatePost(PostUpdateDTO postUpdateDTO, Post post) {
        if (postUpdateDTO.getTitle() != null) post.setTitle(postUpdateDTO.getTitle());
        if (postUpdateDTO.getPostCategory() != null) post.setCategory(postUpdateDTO.getPostCategory());
        if (postUpdateDTO.getContent() != null) post.setBody(postUpdateDTO.getContent());
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Integer postId, Long userId) {
        Post post = postRepository.findByStatusAndPostId(ContentStatus.ACTIVE, postId);

        // 권한 확인
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        // 게시글 상태를 DELETED로 변경
        post.setStatus(ContentStatus.DELETED);
        postRepository.save(post);

        // 관련 댓글 삭제
        deleteComments(post);

        // 관련 사진 삭제
        deletePhotos(post);
    }
}