package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Integer> {
    Optional<PostScrap> findByPostEntityAndUser(PostEntity postEntity, UserEntity user);

    @Query("SELECT ps FROM PostScrap ps WHERE ps.user.userId = :userId AND ps.postEntity.status = 'ACTIVE'")
    List<PostScrap> findActiveScrappedPostsByUserId(@Param("userId") Integer userId);

    boolean existsByPostEntityAndUser(PostEntity post, UserEntity user);
}
