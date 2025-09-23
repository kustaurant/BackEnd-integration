package com.kustaurant.mainapp.evaluation.review;

import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewQueryService reviewQueryService;

    @GetMapping("/web/api/restaurants/{restaurantId}/comments")
    public ResponseEntity<List<ReviewsResponse>> getReviewList(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "POPULARITY") @Parameter(description = "인기순: POPULARITY, 최신순: LATEST") SortOption sort,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments(restaurantId,user.id(),sort);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
