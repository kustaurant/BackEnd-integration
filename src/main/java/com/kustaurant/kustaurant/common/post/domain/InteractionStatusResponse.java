package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.common.post.enums.LikeStatus;
import com.kustaurant.kustaurant.common.post.enums.ScrapStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractionStatusResponse {
    private LikeStatus liked;
    private DislikeStatus disliked;
    private ScrapStatus scrapped;
}

