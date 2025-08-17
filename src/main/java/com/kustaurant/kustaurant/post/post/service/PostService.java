package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.post.comment.infrastructure.jpa.PostCommentReactionRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.PostReactionRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostCommentReactionRepository postCommentReactionRepository;

    // 조회수 증가
    public void increaseVisitCount(Integer postId) {
        postRepository.increaseVisitCount(postId);
    }

    public Post create(PostRequest req, Long userId) {
        Post savedPost = postRepository.save(Post.builder()
                .title(req.title())
                .category(req.category())
                .body(req.content())
                .status(PostStatus.ACTIVE)
                .writerId(userId)
                .visitCount(0)
                .build());

        List<String> imageUrls = ImageExtractor.extract(req.content());

        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(savedPost.getId())
                    .photoImgUrl(imageUrl)
                    .status(PostStatus.ACTIVE)
                    .build());
        }
        return savedPost;
    }

    public void update(Integer postId, PostRequest req,Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException(POST_NOT_FOUND));
        post.ensureWriterBy(userId);

        List<String> imageUrls = ImageExtractor.extract(req.content());
        post.update(req);

        postPhotoRepository.deleteByPostId(postId);
        // ID 기반으로 사진 저장
        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(postId)
                    .photoImgUrl(imageUrl)
                    .status(PostStatus.ACTIVE)
                    .build());
        };
        postRepository.save(post);
    }

    public void delete(Integer postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));
        post.ensureWriterBy(userId);
        post.ensureDeletable();

        postRepository.delete(post.getId());
        postScrapRepository.deleteByPostId(postId);
        postPhotoRepository.deleteByPostId(postId);
        postReactionRepository.deleteByPostId(postId);
        postCommentRepository.deleteByPostId(postId);
        postCommentReactionRepository.deleteByPostId(postId);
    }
}