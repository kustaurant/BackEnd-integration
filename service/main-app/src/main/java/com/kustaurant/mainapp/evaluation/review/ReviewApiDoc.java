package com.kustaurant.mainapp.evaluation.review;

import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewApiDoc {

    @Operation(summary = "평가 댓글 불러오기",
            description = "인기순: POPULARITY, 최신순: LATEST \n\n 기본값: 인기순")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReviewsResponse.class)))}),
            @ApiResponse(responseCode = "400", description = "sort 파라미터 입력값이 올바르지 않을 때 400을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @GetMapping("/v2/restaurants/{restaurantId}/comments")
    ResponseEntity<List<ReviewsResponse>> getReviewList(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "POPULARITY") @Parameter(description = "인기순: POPULARITY, 최신순: LATEST") SortOption sort,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );
}
