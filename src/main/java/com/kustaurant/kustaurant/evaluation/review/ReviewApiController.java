package com.kustaurant.kustaurant.evaluation.review;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewApiController {
    private final ReviewQueryService reviewQueryService;


    // 1. 식당 모든 리뷰 불러오기(평가 & 평가의 댓글들)
    @Operation(summary = "평가 댓글 불러오기",
            description = "인기순: POPULARITY, 최신순: LATEST \n\n 기본값: 인기순")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReviewsResponse.class)))}),
            @ApiResponse(responseCode = "400", description = "sort 파라미터 입력값이 올바르지 않을 때 400을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @GetMapping("/api/v1/restaurants/{restaurantId}/comments")
    public ResponseEntity<List<ReviewsResponse>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "POPULARITY") @Parameter(description = "인기순: POPULARITY, 최신순: LATEST") SortOption sort,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments(restaurantId,user.id(),sort);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
