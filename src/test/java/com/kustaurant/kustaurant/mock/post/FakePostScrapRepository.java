package com.kustaurant.kustaurant.mock.post;

import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakePostScrapRepository implements PostScrapRepository {
    private final ConcurrentMap<PostReactionId, PostScrap> store = new ConcurrentHashMap<>();

    @Override
    public Optional<PostScrap> findById(PostReactionId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(PostScrap postScrap) {
        if (postScrap == null || postScrap.getId() == null) return;
        store.put(postScrap.getId(), postScrap);
    }

    @Override
    public int countByPostId(Long postId) {
        if (postId == null) return 0;
        long cnt = store.values().stream()
                .filter(ps -> ps.getId() != null && postId.equals(ps.getId().postId()))
                .count();
        return (int) cnt;
    }

    @Override
    public void delete(PostScrap postScrap) {
        if (postScrap == null || postScrap.getId() == null) return;
        store.remove(postScrap.getId());
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) return;
        store.keySet().removeIf(key -> postId.equals(key.postId()));
    }
}
