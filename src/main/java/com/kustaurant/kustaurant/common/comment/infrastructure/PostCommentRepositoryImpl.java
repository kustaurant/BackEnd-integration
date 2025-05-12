package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostCommentRepositoryImpl implements PostCommentRepository {
    private final PostCommentJpaRepository postCommentJpaRepository;

    @Override
    public List<PostComment> findActiveByUserId(Integer userId) {
        return postCommentJpaRepository.findActiveByUserIdOrderByCreatedAtDesc(userId).stream().map(PostComment::from).toList();
    }

    @Override
    public List<PostComment> findAll(Specification<PostComment> spec) {
        List<PostCommentEntity> postCommentEntities = postCommentJpaRepository.findAll();
        return postCommentEntities.stream().map(PostCommentEntity::toDomain).toList();
    }

    @Override
    public Optional<PostComment> findById(Integer comment_id) {
        return postCommentJpaRepository.findById(comment_id).map(PostCommentEntity::toDomain);
    }
    @Override
    public Optional<PostComment> findByIdWithReplies(Integer commentId) {
        return postCommentJpaRepository.findById(commentId)
                .map(PostCommentEntity::toDomain);
    }

    @Override
    public PostComment save(PostComment comment) {
        PostCommentEntity entity = postCommentJpaRepository.findById(comment.getCommentId())
                .orElseThrow(() -> new DataNotFoundException("댓글이 존재하지 않습니다."));

        entity.setStatus(comment.getStatus());
        entity.setLikeCount(comment.getNetLikes());

        for (PostComment reply : comment.getReplies()) {
            PostCommentEntity replyEntity = entity.getRepliesList().stream()
                    .filter(r -> r.getCommentId().equals(reply.getCommentId()))
                    .findFirst()
                    .orElseThrow(() -> new DataNotFoundException("대댓글이 존재하지 않습니다."));
            replyEntity.setStatus(reply.getStatus());
            replyEntity.setLikeCount(reply.getNetLikes());
        }

        postCommentJpaRepository.save(entity);
        return entity.toDomain();
    }

}
