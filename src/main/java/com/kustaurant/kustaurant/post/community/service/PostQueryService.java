package com.kustaurant.kustaurant.post.community.service;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.community.controller.response.ParentComment;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.community.infrastructure.PhotoQueryRepository;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostDetailProjection;
import com.kustaurant.kustaurant.post.post.controller.response.PostResponse;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.infrastructure.PostQueryRepository;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostListProjection;
import com.kustaurant.kustaurant.post.post.service.ImageExtractor;
import com.kustaurant.kustaurant.post.post.service.port.PostPhotoRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PostRepository postRepo;

    private static final int PAGE_SIZE = 10;

    public PostResponse getPostForUpdate(Long postId, Long userId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new DataNotFoundException(ErrorCode.POST_NOT_FOUND));
        post.ensureWriterBy(userId);
        List<String> photoUrls = photoQueryRepo.findPostPhotoUrls(postId);

        return PostResponse.from(post, photoUrls);
    }

    public Page<PostListResponse> getPostList(int page, PostCategory category, SortOption sort) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<PostListProjection> projections = postQueryRepo.findPostList(category,sort,pageable);

        return projections.map(PostListResponse::from);
    }

    public PostDetailResponse getPostDetail(Long postId, Long currentUserId) {
        PostDetailProjection projection = postQueryRepo.findPostDetail(postId, currentUserId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.POST_NOT_FOUND));
        List<String> photos = photoQueryRepo.findPostPhotoUrls(postId);
        List<ParentComment> comments = commentTreeService.getCommentTree(postId, currentUserId);

        return PostDetailResponse.from(projection, photos, comments, currentUserId);
    }

    public Page<PostListResponse> searchLatest(int page, String kw) {
        // createdAt DESC, 동일 시각 안정성 위해 postId DESC 보조키 추가
        Sort sort = Sort.by(Sort.Order.desc("createdAt")).and(Sort.by(Sort.Order.desc("postId")));
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

        Page<PostListProjection> projPage = postQueryRepo.searchLatest(kw, pageable);
        return projPage.map(PostListResponse::from);
    }
}
