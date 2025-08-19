package com.kustaurant.kustaurant.post.community.infrastructure;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final QPostPhotoEntity photo = QPostPhotoEntity.postPhotoEntity;

    public List<String> findPostPhotoUrls(Integer postId) {
        return queryFactory.select(photo.photoImgUrl)
                .from(photo)
                .where(photo.postId.eq(postId))
                .orderBy(photo.photoId.asc())
                .fetch();
    }
}