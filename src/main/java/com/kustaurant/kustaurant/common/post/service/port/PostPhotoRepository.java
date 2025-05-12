package com.kustaurant.kustaurant.common.post.service.port;

import com.kustaurant.kustaurant.common.post.domain.PostPhoto;

import java.util.List;

public interface PostPhotoRepository {
    List<PostPhoto> findByPostId(Integer postId);
    void save(PostPhoto postPhoto);
    void deleteById(Integer photoId);
    void deleteByPostId(Integer postId);
    void saveAll(List<PostPhoto> photos);

}