package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.enums.ContentStatus;
import com.kustaurant.kustaurant.common.post.service.port.PostRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;
    @Override
    public Page<Post> findAll(Specification<PostEntity> spec, Pageable pageable) {
        return postJpaRepository.findAll(spec,pageable).map(PostEntity::toDomain);
    }
    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable).map(PostEntity::toDomain);
    }

    @Override
    public List<Post> findAllById(List<Integer> ids) {
        return postJpaRepository.findAllById(ids).stream()
                .map(PostEntity::toDomain)
                .collect(Collectors.toList());
    }
    @Override
    public Page<Post> findByStatus(ContentStatus status, Pageable pageable) {
        return postJpaRepository.findByStatus(status,pageable).map(PostEntity::toDomain);
    }
    @Override
    public List<PostEntity> findActivePostsByUserId(Integer userId) {
        return postJpaRepository.findActivePostsByUserId(userId);
    }
    @Override
    public Optional<PostEntity> findByStatusAndPostId(String status, Integer postId) {
        return postJpaRepository.findByStatusAndPostId(status,postId);
    }
    @Override
    public PostEntity save(PostEntity postEntity){
        return postJpaRepository.save(postEntity);
    }

    // 1. 신규 게시글 저장
    @Override
    public Post save(Post post, Integer userId) {
        if (post.getId() == null) {
            UserEntity userEntity = userJpaRepository.findByUserId(userId)
                    .orElseThrow(() -> new DataNotFoundException("유저가 존재하지 않습니다."));

            PostEntity postEntity = PostEntity.from(post, userEntity);

            // 저장
            PostEntity saved = postJpaRepository.save(postEntity);
            return saved.toDomain();
        } else {
            // 2. 기존 게시글 수정
            return save(post); // 아래 save(post) 재사용!
        }
    }
    @Override
    public Post save(Post post) {
        PostEntity postEntity = postJpaRepository.findById(post.getId())
                .orElseThrow(() -> new DataNotFoundException("게시물이 존재하지 않습니다."));

        // 도메인 → 엔티티 상태 반영
        postEntity.setPostTitle(post.getTitle());
        postEntity.setPostBody(post.getBody());
        postEntity.setPostCategory(post.getCategory());
        postEntity.setStatus(post.getStatus());
        postEntity.setUpdatedAt(LocalDateTime.now());
        postEntity.setNetLikes(post.getNetLikes());
        postEntity.setPostVisitCount(post.getVisitCount());

        // === 댓글/대댓글 상태 반영 (Soft Delete 등) ===
        if (post.getComments() != null && !post.getComments().isEmpty()) {
            for (PostComment comment : post.getComments()) {
                // 1. 댓글 동기화
                PostCommentEntity entity = postEntity.getPostCommentList().stream()
                        .filter(e -> e.getCommentId().equals(comment.getCommentId()))
                        .findFirst()
                        .orElseThrow(() -> new DataNotFoundException("댓글이 존재하지 않습니다."));

                entity.setStatus(comment.getStatus());
                entity.setLikeCount(comment.getNetLikes());

                // 2. 대댓글 동기화
                if (comment.getReplies() != null) {
                    for (PostComment reply : comment.getReplies()) {
                        PostCommentEntity replyEntity = entity.getRepliesList().stream()
                                .filter(r -> r.getCommentId().equals(reply.getCommentId()))
                                .findFirst()
                                .orElseThrow(() -> new DataNotFoundException("대댓글이 존재하지 않습니다."));

                        replyEntity.setStatus(reply.getStatus());
                        replyEntity.setLikeCount(reply.getNetLikes());
                    }
                }
            }
        }


        // 저장 및 반환
        PostEntity saved = postJpaRepository.save(postEntity);
        return saved.toDomain();
    }


    @Override
    public Optional<Post> findById(Integer postId) {
        return postJpaRepository.findById(postId).map(PostEntity::toDomain);
    }

    @Override
    public Optional<Post> findByIdWithComments(Integer postId) {
        return postJpaRepository.findById(postId).map(post->post.toDomain(true,false,false,false,false));
    }

    @Override
    public void increaseVisitCount(Integer postId) {
        PostEntity post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("게시글이 존재하지 않습니다"));
        post.setPostVisitCount(post.getPostVisitCount() + 1);
    }

    @Override
    public void delete(Post post) {

    }

    @Override
    public List<Post> findActiveByUserId(Integer userId) {
        return postJpaRepository.findActivePostsByUserId(userId).stream().map(PostEntity::toDomain).toList();
    }
}
