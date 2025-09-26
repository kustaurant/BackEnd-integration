package com.kustaurant.kustaurant.post.post.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository jpa;


    @Override
    public Post save(Post post) {
        return jpa.save(PostEntity.from(post)).toModel();
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return jpa.findById(postId).map(PostEntity::toModel);
    }

    @Override
    public void increaseVisitCount(Long postId) {
        jpa.incrementViewCount(postId);
    }

    @Override
    public void delete(Long id) {
        jpa.deleteById(id);
    }
}
