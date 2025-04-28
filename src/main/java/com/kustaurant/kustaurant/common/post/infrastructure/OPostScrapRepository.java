package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OPostScrapRepository extends JpaRepository<PostScrapEntity, Integer> {
    Optional<PostScrapEntity> findByPostAndUser(PostEntity post, UserEntity user);

    @Query("SELECT ps FROM PostScrapEntity ps WHERE ps.user.userId = :userId AND ps.post.status = 'ACTIVE'")
    List<PostScrapEntity> findActiveScrappedPostsByUserId(@Param("userId") Integer userId);

    boolean existsByPostAndUser(PostEntity post, UserEntity user);
}
