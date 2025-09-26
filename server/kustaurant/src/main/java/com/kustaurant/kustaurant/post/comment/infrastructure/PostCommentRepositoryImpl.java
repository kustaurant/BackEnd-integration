package com.kustaurant.kustaurant.post.comment.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.COMMENT_NOT_FOUND;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.jpa.PostCommentJpaRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostCommentRepositoryImpl implements PostCommentRepository {
    private final PostCommentJpaRepository jpa;

    @Override
    public Optional<PostComment> findById(Long id) {
        return jpa.findById(id).map(PostCommentEntity::toModel);
    }

    @Override
    public Optional<PostComment> findByIdForUpdate(Long id) {
        return jpa.findByIdForUpdate(id).map(PostCommentEntity::toModel);
    }

    @Override
    public PostComment save(PostComment comment) {
        // 신규 댓글 (id가 null)
        if (comment.getId() == null) {
            PostCommentEntity entity = PostCommentEntity.from(comment);
            jpa.save(entity);
            return entity.toModel();
        }

        // 기존 댓글 수정
        PostCommentEntity entity = jpa.findById(comment.getId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, comment.getId(), "댓글"));

        // 댓글 내용과 상태 업데이트
        PostCommentEntity updatedEntity = PostCommentEntity.builder()
                .parentCommentId(comment.getId())
                .commentBody(comment.getBody())
                .status(comment.getStatus())
                .postId(entity.getPostId())
                .parentCommentId(entity.getParentCommentId())
                .userId(entity.getUserId())
                .build();
        updatedEntity = jpa.save(updatedEntity);
        return updatedEntity.toModel();
    }


    @Override
    public long countActiveRepliesByParentCommentId(Long parentCommentId) {
        return jpa.countActiveRepliesByParentCommentId(parentCommentId);
    }

    @Override
    public long countVisibleRepliesByPostId(Long postId) {
        return jpa.countByPostId(postId);
    }

    @Override
    public void delete(PostComment comment) {
        PostCommentEntity entity = jpa.findById(comment.getId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, comment.getId(), "댓글"));
        jpa.delete(entity);
    }


    @Override
    public void deleteByPostId(Long postId) {
        jpa.deleteByPostId(postId);
    }
}
