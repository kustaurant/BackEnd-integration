//package com.kustaurant.kustaurant.post.post.infrastructure;
//
//import com.kustaurant.kustaurant.post.post.domain.PostLike;
//import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostLikeEntity;
//import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostLikeJpaRepository;
//import com.kustaurant.kustaurant.post.post.service.port.PostLikeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//@RequiredArgsConstructor
//public class PostLikeRepositoryImpl implements PostLikeRepository {
//    private final PostLikeJpaRepository postLikeJpaRepository;
//
//    @Override
//    public Optional<PostLike> findByUserIdAndPostId(Long userId, Integer postId) {
//        return postLikeJpaRepository
//                .findByUserIdAndPostId(userId, postId)
//                .map(PostLikeEntity::toDomain);
//    }
//
//    @Override
//    public Boolean existsByUserIdAndPostId(Long userId, Integer postId) {
//        return postLikeJpaRepository.existsByUserIdAndPostId(userId, postId);
//    }
//
//    @Override
//    public void save(PostLike postLike) {
//        PostLikeEntity entity = PostLikeEntity.from(postLike);
//        postLikeJpaRepository.save(entity);
//    }
//
//    @Override
//    public void deleteByUserIdAndPostId(Long userId, Integer postId) {
//        postLikeJpaRepository.findByUserIdAndPostId(userId, postId)
//                .ifPresent(entity -> {
//                    postLikeJpaRepository.delete(entity);
//                    postLikeJpaRepository.flush(); // 즉시 DB 반영
//                });
//    }
//
//    @Override
//    public int countByPostId(Integer postId) {
//        return postLikeJpaRepository.countByPostId(postId);
//    }
//
//}
