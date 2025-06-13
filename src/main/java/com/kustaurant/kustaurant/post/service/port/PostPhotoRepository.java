package com.kustaurant.kustaurant.post.service.port;

import com.kustaurant.kustaurant.post.domain.PostPhoto;

import java.util.List;

public interface PostPhotoRepository {
    void save(PostPhoto postPhoto);
    void deleteByPost_PostId(Integer postId);
    void saveAll(List<PostPhoto> photos);

}