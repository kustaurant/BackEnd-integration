package com.kustaurant.mainapp.post.post.domain;

import com.kustaurant.mainapp.common.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostReaction {
    private final PostReactionId id;
    private ReactionType reaction;

    public void changeTo(ReactionType newReaction) {
        this.reaction = newReaction;
    }
}
