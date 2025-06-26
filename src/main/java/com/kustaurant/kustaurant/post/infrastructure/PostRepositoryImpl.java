package com.kustaurant.kustaurant.post.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.comment.domain.PostComment;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.post.domain.Post;
import com.kustaurant.kustaurant.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
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
    public List<PostEntity> findActivePostsByUserId(Long userId) {
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


    @Override
    public Post save(Post post) {
        UserEntity userEntity = userJpaRepository.findByUserId(post.getAuthorId())
                .orElseThrow(() -> new DataNotFoundException(USER_NOT_FOUND, post.getAuthorId(), "유저"));

        PostEntity postEntity;
        if (post.getId() == null) {
            postEntity = PostEntity.from(post, userEntity);
            postEntity.setCreatedAt(LocalDateTime.now());
            postEntity.setUpdatedAt(LocalDateTime.now());
        } else {
            postEntity = postJpaRepository.findById(post.getId())
                    .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUNT, post.getId(), "게시물"));

            // 엔티티 상태 동기화
            postEntity.setPostTitle(post.getTitle());
            postEntity.setPostBody(post.getBody());
            postEntity.setPostCategory(post.getCategory());
            postEntity.setStatus(post.getStatus());
            postEntity.setUpdatedAt(LocalDateTime.now());
            postEntity.setNetLikes(post.getNetLikes());
            postEntity.setPostVisitCount(post.getVisitCount());

            // === 댓글 및 대댓글 상태 동기화 ===
            if (post.getComments() != null && !post.getComments().isEmpty()) {
                for (PostComment comment : post.getComments()) {
                    // 댓글 엔티티 찾기
                    PostCommentEntity entity = postEntity.getPostCommentList().stream()
                            .filter(e -> e.getCommentId().equals(comment.getCommentId()))
                            .findFirst()
                            .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, comment.getCommentId(), "댓글"));

                    // 댓글 상태 동기화 (예: soft delete, netLikes 등)
                    entity.setStatus(comment.getStatus());
                    entity.setLikeCount(comment.getNetLikes());

                    // 대댓글(2 depth) 동기화
                    if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                        for (PostComment reply : comment.getReplies()) {
                            PostCommentEntity replyEntity = entity.getRepliesList().stream()
                                    .filter(r -> r.getCommentId().equals(reply.getCommentId()))
                                    .findFirst()
                                    .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, "대댓글이 존재하지 않습니다."));

                            replyEntity.setStatus(reply.getStatus());
                            replyEntity.setLikeCount(reply.getNetLikes());
                        }
                    }
                }
            }
        }

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
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUNT, postId, "게시글"));
        post.setPostVisitCount(post.getPostVisitCount() + 1);
    }

    @Override
    public void delete(Post post) {

    }

    @Override
    public List<Post> findActiveByUserId(Long userId) {
        return postJpaRepository.findActivePostsByUserId(userId).stream().map(PostEntity::toDomain).toList();
    }
}
