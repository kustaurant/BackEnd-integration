//package com.kustaurant.kustaurant.post.post.domain;
//
//import lombok.Builder;
//import lombok.Getter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Builder
//public class PostLike {
//
//    private Integer id;
//    private Long userId;
//    private Integer postId;
//    private final LocalDateTime createdAt;
//
//    public PostLike(Integer id, Long userId, Integer postId, LocalDateTime createdAt) {
//        this.id = id;
//        this.userId = userId;
//        this.postId = postId;
//        this.createdAt = createdAt;
//    }
//
//    public PostLike(Long userId, Integer postId, LocalDateTime createdAt) {
//        this.userId = userId;
//        this.postId = postId;
//        this.createdAt = createdAt;
//    }
//
//}