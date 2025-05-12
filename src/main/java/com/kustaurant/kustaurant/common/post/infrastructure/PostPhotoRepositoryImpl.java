package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostPhoto;
import com.kustaurant.kustaurant.common.post.service.port.PostPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostPhotoRepositoryImpl implements PostPhotoRepository {
    private final PostPhotoJpaRepository postPhotoJpaRepository;

    @Override
    public List<PostPhoto> findByPostId(Integer postId) {
        return postPhotoJpaRepository.findByPostId(postId).stream()
                .map(PostPhotoEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(PostPhoto postPhoto) {
        // PostPhotoEntity 변환 시 PostEntity가 필요하다면 추가적으로 처리해야 함
        throw new UnsupportedOperationException("PostEntity 주입 로직 필요");
    }

    @Override
    public void deleteById(Integer photoId) {
        postPhotoJpaRepository.deleteById(photoId);
    }

    @Override
    public void deleteByPostId(Integer postId) {
        postPhotoJpaRepository.deleteByPostId(postId);
    }
}