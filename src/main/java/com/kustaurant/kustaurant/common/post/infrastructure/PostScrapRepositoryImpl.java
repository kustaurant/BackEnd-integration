package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
