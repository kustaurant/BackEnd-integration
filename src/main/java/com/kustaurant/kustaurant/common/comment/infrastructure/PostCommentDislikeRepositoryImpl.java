package com.kustaurant.kustaurant.common.comment.infrastructure;

import com.kustaurant.kustaurant.common.comment.service.port.PostCommentDislikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostCommentDislikeRepositoryImpl implements PostCommentDislikeRepository {
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;

    @Override
    public void save(PostCommentDislike dislike) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public List<PostCommentDislike> findByCommentId(Integer commentId) {
        return List.of();
    }

    @Override
    public Optional<PostCommentDislike> findByUserIdAndCommentId(Integer userId, Integer commentId) {
        return Optional.empty();
    }
}
