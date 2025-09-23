package com.kustaurant.mainapp.evaluation.review;

import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewApiController implements ReviewApiDoc {
    private final ReviewQueryService reviewQueryService;


    // 1. 식당 모든 리뷰 불러오기(평가 & 평가의 댓글들)
    @GetMapping("/v2/restaurants/{restaurantId}/comments")
    public ResponseEntity<List<ReviewsResponse>> getReviewList(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "POPULARITY") SortOption sort,
            @AuthUser AuthUserInfo user
    ) {
        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments(restaurantId,user.id(),sort);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
