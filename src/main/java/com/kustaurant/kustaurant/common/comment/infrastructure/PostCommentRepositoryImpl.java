package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostCommentRepositoryImpl implements PostCommentRepository {
    private final PostCommentJpaRepository postCommentJpaRepository;

    @Override
    public List<PostComment> findActiveByUserId(Integer userId) {
        return postCommentJpaRepository.findActiveByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PostComment::from)
                .toList();
    }
}
