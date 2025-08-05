//package com.kustaurant.kustaurant.post.post.infrastructure.jpa;
//
//import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostDislikeEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface PostDislikeJpaRepository extends JpaRepository<PostDislikeEntity,Integer> {
//    Optional<PostDislikeEntity> findByUserIdAndPostId(Long userId, Integer postId);
//
//    boolean existsByUserIdAndPostId(Long userId, Integer postId);
//
//    int countByPostId(Integer postId);
//}
