package com.kustaurant.kustaurant.common.post.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostPhoto {
    private Integer id;
    private final String photoImgUrl;
    private String status;

    public PostPhoto(String photoImgUrl, String status) {
        this.photoImgUrl = photoImgUrl;
        this.status = status;
    }
}