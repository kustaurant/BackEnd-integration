package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.post.domain.PostDislike;
import com.kustaurant.kustaurant.post.service.port.PostDislikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDislikeRepositoryImpl implements PostDislikeRepository {
    private final PostDislikeJpaRepository postDislikeJpaRepository;


    @Override
    public Optional<PostDislike> findByUserIdAndPostId(Integer userId, Integer postId) {

        return postDislikeJpaRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .map(PostDislikeEntity::toDomain);
    }

    @Override
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postDislikeJpaRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public PostDislike save(PostDislike postDislike) {
        return postDislikeJpaRepository.save(PostDislikeEntity.from(postDislike)).toDomain();
    }

    @Override
    public void deleteByUserIdAndPostId(Integer userId, Integer postId) {
        postDislikeJpaRepository.findByUser_UserIdAndPost_PostId(userId, postId)
                .ifPresent(postDislikeJpaRepository::delete);
    }

    @Override
    public int countByPostId(Integer postId) {
        return postDislikeJpaRepository.countByPost_PostId(postId);
    }

}
