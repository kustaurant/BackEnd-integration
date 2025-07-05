package com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    List<PostPhotoEntity> findByPostId(Integer postId);
}