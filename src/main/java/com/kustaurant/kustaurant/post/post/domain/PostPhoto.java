package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostPhoto {
    private Integer id;
    private Integer postId;
    private String photoImgUrl;
    private ContentStatus status;
}