package com.kustaurant.kustaurant.common.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    void deleteByPost_PostId(Integer postId);
}