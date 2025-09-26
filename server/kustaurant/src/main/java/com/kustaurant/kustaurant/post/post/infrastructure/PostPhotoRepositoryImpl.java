package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostPhotoJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostPhotoRepositoryImpl implements PostPhotoRepository {
    private final PostPhotoJpaRepository jpa;
    
    @Override
    public void save(PostPhoto postPhoto) {
        PostPhotoEntity entity = PostPhotoEntity.from(postPhoto);
        jpa.save(entity);
    }

    @Override
    public void saveAll(List<PostPhoto> photos) {
        List<PostPhotoEntity> entities = photos.stream()
                .map(PostPhotoEntity::from)
                .toList();
        jpa.saveAll(entities);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jpa.deleteByPostId(postId);
    }
}