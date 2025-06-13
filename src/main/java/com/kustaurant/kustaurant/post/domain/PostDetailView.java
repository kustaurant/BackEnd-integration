package com.kustaurant.kustaurant.post.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailView {
    private PostDTO post;
    private UserDTO currentUser;
    private String sort;
    private InteractionStatusResponse postInteractionStatus;
    private Map<Integer, InteractionStatusResponse> commentInteractionMap;

}

