package com.kustaurant.kustaurant.common.post.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final JpaPostRepository jpaPostRepository;

    public Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable) {
        return jpaPostRepository.findAll(spec,pageable);
    }

    public Page<PostEntity> findAll(Pageable pageable) {
        return jpaPostRepository.findAll(pageable);
    }

    public Page<PostEntity> findByStatus(String status, Pageable pageable) {
        return jpaPostRepository.findByStatus(status,pageable);
    }

    public List<PostEntity> findActivePostsByUserId(Integer userId) {
        return jpaPostRepository.findActivePostsByUserId(userId);
    }

    public Optional<PostEntity> findByStatusAndPostId(String status, Integer postId) {
        return jpaPostRepository.findByStatusAndPostId(status,postId);
    }

    public PostEntity save(PostEntity postEntity){
        return jpaPostRepository.save(postEntity);
    }

    @Override
    public Optional<PostEntity> findById(Integer postId) {
        return jpaPostRepository.findById(postId);
    }
}
