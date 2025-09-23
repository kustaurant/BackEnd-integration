package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.post.comment.service.port.PostCommentReactionRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostReactionRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.user.mypage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostCommentReactionRepository postCommentReactionRepository;

    private final UserStatsService userStatsService;

    public Post create(PostRequest req, Long userId) {
        Post savedPost = postRepository.save(Post.create(userId, req));

        List<String> imgUrls = ImageExtractor.extract(req.content());

        if(!imgUrls.isEmpty()) {
            List<PostPhoto> photos = imgUrls.stream()
                    .map(url -> PostPhoto.builder()
                            .postId(savedPost.getId())
                            .photoImgUrl(url)
                            .status(PostStatus.ACTIVE)
                            .build())
                    .toList();

            postPhotoRepository.saveAll(photos);
        }

        userStatsService.incPost(userId);
        return savedPost;
    }

    public Post update(Long postId, PostRequest req,Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException(POST_NOT_FOUND));
        post.ensureWriterBy(userId);
        Post update = post.update(postId, req);

        List<String> imageUrls = ImageExtractor.extract(req.content());
        postPhotoRepository.deleteByPostId(postId);
        // ID 기반으로 사진 저장
        for (String url : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(postId)
                    .photoImgUrl(url)
                    .status(PostStatus.ACTIVE)
                    .build());
        };
        return postRepository.save(update);
    }

    public void delete(Long postId, Long userId) {
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
        userStatsService.decPost(userId);
    }
}