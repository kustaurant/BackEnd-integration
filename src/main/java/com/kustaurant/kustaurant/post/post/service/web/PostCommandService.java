package com.kustaurant.kustaurant.post.post.service.web;

import com.kustaurant.kustaurant.post.post.domain.ImageExtractor;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostDislike;
import com.kustaurant.kustaurant.post.post.domain.PostLike;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostDislikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostLikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommandService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final ImageExtractor imageExtractor;

    // 조회수 증가
    @Transactional
    public void increaseVisitCount(Integer postId) {
        postRepository.increaseVisitCount(postId);
    }

    @Transactional
    public ReactionToggleResponse toggleLike(Integer postId, Long userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.LIKE_DELETED;
        } else if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_TO_LIKE;
        } else {
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_CREATED;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }

    @Transactional
    public ReactionToggleResponse toggleDislike(Integer postId, Long userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.DISLIKE_DELETED;
        } else if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_TO_DISLIKE;
        } else {
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_CREATED;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }

    @Transactional
    public void deletePost(Integer postId) {
        Post post = postRepository.findByIdWithComments(postId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));

        // 게시물 상태 변경
        post.delete();
        
        // 댓글 삭제 (ID 기반으로 처리)
        // 댓글들은 별도 서비스에서 처리하므로 여기서는 게시글만 삭제

        // 스크랩 삭제
        postScrapRepository.deleteByPostId(postId);

        // 사진 삭제
        postPhotoRepository.deleteByPostId(postId);

        // 저장
        postRepository.save(post);
    }

    @Transactional
    public Post create(String title, String category, String body, Long userId) {
        Post post = Post.builder().title(title).category(category).body(body).status(ContentStatus.ACTIVE).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).authorId(userId).visitCount(0).build();
        Post savedPost = postRepository.save(post);

        List<String> imageUrls = imageExtractor.extract(body);

        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(savedPost.getId())
                    .photoImgUrl(imageUrl)
                    .status(ContentStatus.ACTIVE)
                    .build());
        }
        return savedPost;
    }

    @Transactional
    public void update(Integer postId, String title, String category, String body) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<String> imageUrls = imageExtractor.extract(body);
        post.update(title, body, category, imageUrls);
        post.setUpdatedAt(LocalDateTime.now());

        postPhotoRepository.deleteByPostId(postId);
        // ID 기반으로 사진 저장
        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(postId)
                    .photoImgUrl(imageUrl)
                    .status(ContentStatus.ACTIVE)
                    .build());
        };
        postRepository.save(post);
    }
}