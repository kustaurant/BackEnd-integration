package com.kustaurant.mainapp.post.comment.domain;


import com.kustaurant.mainapp.common.enums.ReactionType;
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
