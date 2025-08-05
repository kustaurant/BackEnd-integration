//package com.kustaurant.kustaurant.post.post.service.port;
//
//import com.kustaurant.kustaurant.post.post.domain.PostLike;
//
//import java.util.Optional;
//
//public interface PostLikeRepository {
//    Optional<PostLike> findByUserIdAndPostId(Long userId, Integer postId);
//
//    Boolean existsByUserIdAndPostId(Long userId, Integer postId);
//
//    void save(PostLike postLike);
//
//    void deleteByUserIdAndPostId(Long userId, Integer postId);
//
//    int countByPostId(Integer postId);
//}
