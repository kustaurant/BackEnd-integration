package com.kustaurant.mainapp.post.community.infrastructure;

import com.kustaurant.mainapp.post.post.infrastructure.entity.QPostPhotoEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final QPostPhotoEntity photo = QPostPhotoEntity.postPhotoEntity;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<String> findPostPhotoUrls(Long postId) {
        return queryFactory.select(photo.photoImgUrl)
                .from(photo)
                .where(photo.postId.eq(postId))
                .orderBy(photo.photoId.asc())
                .fetch();
    }
}