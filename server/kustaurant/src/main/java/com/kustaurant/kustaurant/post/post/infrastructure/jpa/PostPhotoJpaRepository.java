package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    void deleteByPostId(Long postId);
}