package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
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
}
