package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostScrapRepositoryImpl implements PostScrapRepository {
    private final PostScrapJpaRepository postScrapJpaRepository;

    @Override
    public List<PostScrap> findByUserId(Integer userId) {
        return postScrapJpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(PostScrap::from)
                .toList();
    }

    @Override
    public Optional<PostScrapEntity> findByPostAndUser(PostEntity post, UserEntity user) {
        return postScrapJpaRepository.findByPostAndUser(post,user);
    }

    @Override
    public List<PostScrapEntity> findActiveScrappedPostsByUserId(Integer userId) {
        return postScrapJpaRepository.findActiveScrappedPostsByUserId(userId);
    }

    @Override
    public boolean existsByUserAndPost(User user, Post post) {
        return postScrapJpaRepository.existsByUserAndPost(user,post);
    }

    @Override
    public void delete(PostScrap postScrap) {

    }

    @Override
    public void save(PostScrap postScrap) {

    }
}
