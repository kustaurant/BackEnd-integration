package com.kustaurant.kustaurant.post.community.service;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.community.controller.response.ParentComment;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.community.infrastructure.PhotoQueryRepository;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostDetailProjection;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.infrastructure.PostQueryRepository;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostListProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostQueryRepository postQueryRepo;
    private final PhotoQueryRepository photoQueryRepo;
    private final CommentTreeService commentTreeService;

    public Page<PostListResponse> getPostList(int page, int size, PostCategory category, SortOption sort) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostListProjection> projections = postQueryRepo.findPostList(category,sort,pageable);

        return projections.map(PostListResponse::from);
    }

    public PostDetailResponse getPostDetail(Integer postId, Long currentUserId) {
        PostDetailProjection projection = postQueryRepo.findPostDetail(postId, currentUserId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.POST_NOT_FOUND));
        List<String> photos = photoQueryRepo.findPostPhotoUrls(postId);
        List<ParentComment> comments = commentTreeService.getCommentTree(postId, currentUserId);

        return PostDetailResponse.from(projection, photos, comments, currentUserId);
    }
}
