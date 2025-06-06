package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailView {
    private PostDTO post;
    private User currentUser;
    private String sort;
    private InteractionStatusResponse postInteractionStatus;
    private Map<Integer, InteractionStatusResponse> commentInteractionMap;

}

