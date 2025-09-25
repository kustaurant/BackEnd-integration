package com.kustaurant.kustaurant.post.comment.domain;


import com.kustaurant.kustaurant.common.enums.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostCommentReaction {
    private final PostCommentReactionId id;
    private ReactionType reaction;

    public void changeTo(ReactionType newReaction) {
        this.reaction = newReaction;
    }
}
