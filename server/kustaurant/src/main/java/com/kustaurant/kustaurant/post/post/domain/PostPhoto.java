package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostPhoto {
    private Integer id;
    private Long postId;
    private String photoImgUrl;
    private PostStatus status;
}