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
    private final PostJpaRepository postJpaRepository;

    public Page<PostEntity> findAll(Specification<PostEntity> spec, Pageable pageable) {
        return postJpaRepository.findAll(spec,pageable);
    }

    public Page<PostEntity> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable);
    }

    public Page<PostEntity> findByStatus(String status, Pageable pageable) {
        return postJpaRepository.findByStatus(status,pageable);
    }

    public List<PostEntity> findActivePostsByUserId(Integer userId) {
        return postJpaRepository.findActivePostsByUserId(userId);
    }

    public Optional<PostEntity> findByStatusAndPostId(String status, Integer postId) {
        return postJpaRepository.findByStatusAndPostId(status,postId);
    }

    public PostEntity save(PostEntity postEntity){
        return postJpaRepository.save(postEntity);
    }

    @Override
    public Optional<PostEntity> findById(Integer postId) {
        return postJpaRepository.findById(postId);
    }
}
