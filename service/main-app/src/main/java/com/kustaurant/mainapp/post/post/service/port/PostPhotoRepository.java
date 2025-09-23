package com.kustaurant.mainapp.post.post.service.port;

import com.kustaurant.mainapp.post.post.domain.PostPhoto;

import java.util.List;

public interface PostPhotoRepository {
    void save(PostPhoto postPhoto);
    void saveAll(List<PostPhoto> photos);
    void deleteByPostId(Long postId);
}