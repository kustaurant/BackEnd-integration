package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.infrastructure.PostQueryRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OPostQueryService {
    private final PostQueryRepository postQueryRepository;

    // 검색결과 반환
    public Page<PostDetailResponse> getList(int page, SortOption sort, String kw, PostCategory postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));

        if (sort == SortOption.POPULARITY) { // 인기순 정렬
            return postQueryRepository.findPopularPostsBySearchKeywordWithAllData(kw, postCategory, pageable, null, POPULARCOUNT)
                    .map(PostDetailResponse::from);
        } else { // 최신순 정렬
            return postQueryRepository.findPostsBySearchKeywordWithAllData(kw, postCategory, pageable, null)
                    .map(PostDetailResponse::from);
        }
    }

}