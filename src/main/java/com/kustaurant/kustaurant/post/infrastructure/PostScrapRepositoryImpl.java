package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostScrapRepositoryImpl implements PostScrapRepository {
    private final PostScrapJpaRepository postScrapJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postJpaRepository;
    @Override
    public List<PostScrap> findByUserId(Integer userId) {
        return postScrapJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(PostScrap::from).toList();
    }

    @Override
    public boolean existsByUserIdAndPostId(Integer userId, Integer postId) {
        return postScrapJpaRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Override
    public void delete(PostScrap postScrap) {
        postScrapJpaRepository.findByUser_UserIdAndPost_PostId(postScrap.getUserId(), postScrap.getPostId())
                .ifPresent(postScrapJpaRepository::delete);
    }


    @Override
    public void deleteByPostId(Integer postId) {
        postScrapJpaRepository.deleteByPost_PostId(postId);
    }

    @Override
    public Optional<PostScrap> findByUserIdAndPostId(Integer userId, Integer postId) {
        return postScrapJpaRepository.findByUser_UserIdAndPost_PostId(userId, postId).map(PostScrapEntity::toDomain);
    }

    @Override
    public void save(PostScrap postScrap) {
        UserEntity userEntity = userJpaRepository.findByUserId(postScrap.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        PostEntity postEntity = postJpaRepository.findById(postScrap.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostScrapEntity postScrapEntity = PostScrapEntity.builder()
                .user(userEntity)
                .post(postEntity)
                .createdAt(postScrap.getCreatedAt())
                .build();

        postScrapJpaRepository.save(postScrapEntity);
    }
}
