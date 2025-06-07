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
    private final PostJpaRepository postJpaRepository;
    @Override
    public void save(PostPhoto postPhoto) {
        PostEntity postEntity = postJpaRepository.findById(postPhoto.getId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        PostPhotoEntity entity = PostPhotoEntity.from(postPhoto, postEntity);
        postPhotoJpaRepository.save(entity);
    }


    @Override
    public void deleteByPost_PostId(Integer postId) {
        postPhotoJpaRepository.deleteByPost_PostId(postId);
    }

    @Override
    public void saveAll(List<PostPhoto> photos) {
    }
}