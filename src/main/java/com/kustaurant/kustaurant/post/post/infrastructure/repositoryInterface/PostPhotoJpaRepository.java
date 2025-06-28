package com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    void deleteByPost_PostId(Integer postId);
}