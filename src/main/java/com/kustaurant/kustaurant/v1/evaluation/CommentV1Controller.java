package com.kustaurant.kustaurant.v1.evaluation;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.evaluation.review.ReviewQueryService;
import com.kustaurant.kustaurant.evaluation.review.ReviewsResponse;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.v1.evaluation.response.RestaurantCommentDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentV1Controller {

    private final ReviewQueryService reviewQueryService;

    // 리뷰 불러오기
    @GetMapping("/restaurants/{restaurantId}/comments")
    public ResponseEntity<List<RestaurantCommentDTO>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "popularity")
            String sort,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @AuthUser AuthUserInfo user
    ) {
        // sort 파라미터 체크
        if (!sort.equals("popularity") && !sort.equals("latest")) {
            throw new ParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
        }

        SortOption sortOption = sort.equalsIgnoreCase("popularity") ? SortOption.POPULARITY : SortOption.LATEST;
        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments((long) restaurantId, user.id(), sortOption);

        return new ResponseEntity<>(response.stream().map(RestaurantCommentDTO::fromV2).toList(), HttpStatus.OK);
    }
}
