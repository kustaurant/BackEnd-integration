package com.kustaurant.kustaurant.post.post.controller.response;

import com.kustaurant.kustaurant.post.post.domain.enums.depricated.DislikeStatus;
import com.kustaurant.kustaurant.post.post.domain.enums.depricated.LikeStatus;
import com.kustaurant.kustaurant.post.post.domain.enums.ScrapStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractionStatusResponse {
    private LikeStatus liked;
    private DislikeStatus disliked;
    private ScrapStatus scrapped;
}

