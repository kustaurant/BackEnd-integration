package com.kustaurant.kustaurant.mock.post;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReaction;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReactionId;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentReactionJpaId;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentReactionRepository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakePostCommentReactionRepository implements PostCommentReactionRepository {
    private final ConcurrentMap<PostCommentReactionId, PostCommentReaction> store = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Long> commentToPost = new ConcurrentHashMap<>();

    @Override
    public Optional<PostCommentReaction> findById(PostCommentReactionId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public PostCommentReaction save(PostCommentReaction postCommentReaction) {
        if (postCommentReaction == null || postCommentReaction.getId() == null) {
            throw new IllegalArgumentException("postCommentReaction or id is null");
        }
        store.put(postCommentReaction.getId(), postCommentReaction);
        return postCommentReaction;
    }

    @Override
    public void deleteById(PostCommentReactionId id) {
        if (id == null) return;
        store.remove(id);
    }

    @Override
    public int countByPostCommentIdAndReaction(Long postCommentId, ReactionType reaction) {
        if (postCommentId == null || reaction == null) return 0;
        long cnt = store.values().stream()
                .filter(r -> r.getId() != null)
                .filter(r -> Objects.equals(r.getId().postCommentId(), postCommentId))
                .filter(r -> r.getReaction() == reaction)
                .count();
        return (int) cnt;
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) return;
        store.entrySet().removeIf(e -> {
            PostCommentReaction pcr = e.getValue();
            if (pcr == null || pcr.getId() == null) return false;
            Long pcid = pcr.getId().postCommentId();
            return Objects.equals(commentToPost.get(pcid), postId);
        });
    }

    public void indexComment(long postCommentId, long postId) {
        commentToPost.put(postCommentId, postId);
    }
}
