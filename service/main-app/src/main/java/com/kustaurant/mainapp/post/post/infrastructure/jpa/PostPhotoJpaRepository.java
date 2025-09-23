package com.kustaurant.mainapp.post.post.infrastructure.jpa;

import com.kustaurant.mainapp.post.post.infrastructure.entity.PostPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    void deleteByPostId(Long postId);
}