package com.kustaurant.kustaurant.common.post.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostPhoto {
    private final Integer id;
    private final String photoImgUrl;
    private final String status;
}