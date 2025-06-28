package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
import com.kustaurant.kustaurant.post.post.domain.response.InteractionStatusResponse;
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

