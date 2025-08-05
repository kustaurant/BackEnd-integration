//package com.kustaurant.kustaurant.post.post.infrastructure.jpa;
//
//import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostLikeEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity,Integer> {
//    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Integer postId);
//
//    Boolean existsByUserIdAndPostId(Long userId, Integer postId);
//
//    int countByPostId(Integer postId);
//}
