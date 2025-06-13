package com.kustaurant.kustaurant.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostPhotoJpaRepository extends JpaRepository<PostPhotoEntity, Integer> {
    void deleteByPost_PostId(Integer postId);
}