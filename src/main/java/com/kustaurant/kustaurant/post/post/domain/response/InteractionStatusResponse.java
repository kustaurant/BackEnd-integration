package com.kustaurant.kustaurant.post.post.domain.response;

import com.kustaurant.kustaurant.post.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.post.post.enums.LikeStatus;
import com.kustaurant.kustaurant.post.post.enums.ScrapStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InteractionStatusResponse {
    private LikeStatus liked;
    private DislikeStatus disliked;
    private ScrapStatus scrapped;
}

