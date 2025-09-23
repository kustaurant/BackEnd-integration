package com.kustaurant.kustaurant.post.post.infrastructure.jpa;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<PostEntity> findById(Long aLong);

    Page<PostEntity> findAll(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostEntity p set p.postVisitCount = p.postVisitCount + 1 where p.postId = :id")
    void incrementViewCount(@Param("id") long id);
}
