package com.kustaurant.kustaurant.post.comment.infrastructure.repo;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.COMMENT_NOT_FOUND;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.jpa.PostCommentJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostCommentRepositoryImpl implements PostCommentRepository {
    private final PostCommentJpaRepository postCommentJpaRepository;

    @Override
    public List<PostComment> findActiveByUserId(Long userId) {
        return postCommentJpaRepository.findActiveByUserIdOrderByCreatedAtDesc(userId).stream().map(PostCommentEntity::toModel).toList();
    }

    @Override
    public List<PostComment> findParentComments(Integer postId) {
        // ID 기반으로 부모 댓글 조회
        List<PostCommentEntity> parentComments = postCommentJpaRepository.findByPostIdAndStatus(postId, PostStatus.ACTIVE)
                .stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .collect(Collectors.toList());

        return parentComments.stream()
                .map(PostCommentEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PostComment> findById(Integer comment_id) {
        return postCommentJpaRepository.findById(comment_id).map(PostCommentEntity::toModel);
    }

    @Override
    public Optional<PostComment> findByIdWithReplies(Integer commentId) {
        return postCommentJpaRepository.findById(commentId)
                .map(PostCommentEntity::toModel);
    }

    @Override
    public PostComment save(PostComment comment) {
        // 신규 댓글 (id가 null)
        if (comment.getId() == null) {
            PostCommentEntity entity = PostCommentEntity.from(comment);
            postCommentJpaRepository.save(entity);
            return entity.toModel();
        }

        // 기존 댓글 수정
        PostCommentEntity entity = postCommentJpaRepository.findById(comment.getId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, comment.getId(), "댓글"));

//        entity.setStatus(comment.getStatus());
        // likeCount 필드 제거됨

        postCommentJpaRepository.save(entity);
        return entity.toModel();
    }

    @Override
    public void deleteByPostId(Integer postId) {
        // Soft delete 방식으로 변경
        List<PostCommentEntity> comments = postCommentJpaRepository.findByPostId(postId);
        for (PostCommentEntity comment : comments) {
//            comment.setStatus(PostStatus.DELETED);
        }
        postCommentJpaRepository.saveAll(comments);
    }

    @Override
    public List<PostComment> findByPostId(Integer postId) {
        return postCommentJpaRepository.findByPostId(postId).stream()
                .map(PostCommentEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostComment> findByParentCommentId(Integer parentCommentId) {
        return List.of();
    }

    @Override
    public List<PostComment> saveAll(List<PostComment> comments) {
        return List.of();
    }
}
