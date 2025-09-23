package com.kustaurant.mainapp.post.post.infrastructure;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.post.post.domain.Post;
import com.kustaurant.mainapp.post.post.domain.PostReactionId;
import com.kustaurant.mainapp.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.mainapp.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.mainapp.post.post.infrastructure.jpa.PostJpaRepository;
import com.kustaurant.mainapp.post.post.service.port.PostRepository;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
