package com.kustaurant.mainapp.mock.post;

import com.kustaurant.mainapp.post.post.domain.PostPhoto;
import com.kustaurant.mainapp.post.post.service.port.PostPhotoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakePostPhotoRepository implements PostPhotoRepository {
    private final ConcurrentMap<Long, List<PostPhoto>> store = new ConcurrentHashMap<>();
    @Override
    public void save(PostPhoto postPhoto) {
        if (postPhoto == null) return;
        Long postId = postPhoto.getPostId();
        if (postId == null) return;

        store.computeIfAbsent(postId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(postPhoto);
    }

    @Override
    public void saveAll(List<PostPhoto> photos) {
        if (photos == null || photos.isEmpty()) return;
        for (PostPhoto p : photos) save(p);
    }

    @Override
    public void deleteByPostId(Long postId) {
        if (postId == null) return;
        store.remove(postId);
    }

    public List<PostPhoto> findByPostId(Long postId) {
        if (postId == null) return List.of();
        List<PostPhoto> list = store.get(postId);
        if (list == null) return List.of();
        return List.copyOf(list);
    }
}
