package com.kustaurant.kustaurant.mock.post;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.service.port.PostReactionRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakePostReactionRepository implements PostReactionRepository {
    private final ConcurrentMap<PostReactionId, PostReaction> store = new ConcurrentHashMap<>();
    @Override
    public Optional<PostReaction> findById(PostReactionId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public PostReaction save(PostReaction postReaction) {
        if (postReaction == null || postReaction.getId() == null) {
            throw new IllegalArgumentException("postReaction or id is null");
        }
        store.put(postReaction.getId(), postReaction);
        return postReaction;
    }

    @Override
    public void delete(PostReaction postReaction) {
        PostReactionId id = postReaction.getId();
        if(id == null) return;
        store.remove(id);
    }

    @Override
    public int countByPostIdAndReaction(Long postId, ReactionType reaction) {
        if (postId == null || reaction == null) return 0;
        long cnt = store.values().stream()
                .filter(pr -> pr.getId() != null
                && postId.equals(pr.getId().postId())
                && reaction.equals(pr.getReaction()))
                .count();
        return (int) cnt;
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) return;
        store.keySet().removeIf(k -> postId.equals(k.postId()));
    }
}
