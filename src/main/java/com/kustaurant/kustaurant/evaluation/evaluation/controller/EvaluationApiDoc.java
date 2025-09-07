package com.kustaurant.kustaurant.evaluation.evaluation.controller;

import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface EvaluationApiDoc {

    @Operation(summary = "평가 하기로 갈 때 이전 평가 데이터가 있을 경우 불러오기", description = "평가하기에서 사용하는 형식과 동일합니다. 유저가 이전에 해당 식당을 평가했을 경우 이전 평가 데이터를 불러와서 이전에 평가했던 사항을 보여줍니다. " +
            "\n\n이전 데이터가 없을 경우 아무것도 반환하지 않습니다.\n\n현재 restaurantId 599, 631에 데이터 있습니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - evaluationScore: **null일 수 있습니다.**\n\n" +
            "   - evaluationSituations: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - evaluationImgUrl: **null일 수 있습니다.**\n\n" +
            "   - evaluationComment: **null일 수 있습니다.**\n\n" +
            "   - starComment: not null\n\n" +
            "   - newImage: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)", content = {@Content(schema = @Schema(implementation = EvaluationDTO.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @GetMapping("/v2/auth/restaurants/{restaurantId}/evaluation")
    ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );

    @PostMapping(value = "/v2/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "평가하기", description = "평가하기 입니다.\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - evaluationScore: 필수\n\n" +
            "   - evaluationSituations: 사용자가 상황을 선택했을 경우만. 없어도 됩니다.\n\n" +
            "   - evaluationImgUrl: 사용 안함. 없어도 됩니다.\n\n" +
            "   - evaluationComment: 사용자가 코멘트를 입력했을 경우만. 없어도 됩니다.\n\n" +
            "   - starComments: 사용 안함. 없어도 됩니다.\n\n" +
            "   - newImage: 사용자가 이미지를 추가했을 경우만. 없어도 됩니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - evalId: not null\n\n" +
            "   - evalScore: not null\n\n" +
            "   - writerIconImgUrl: not null\n\n" +
            "   - commentNickname: not null\n\n" +
            "   - commentTime: not null\n\n" +
            "   - evalImgUrl: **null일 수 있습니다.**\n\n" +
            "   - evalBody: **null일 수 있습니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - likeCount: not null\n\n" +
            "   - dislikeCount: not null\n\n" +
            "   - evalCommentList: **null이거나 빈 배열일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "평가하기가 정상적으로 처리되었을 경우 204를 반환합니다.", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = EvalCommentResponse.class)))}),
            @ApiResponse(responseCode = "400", description = "평가 점수가 없거나 0점인 경우 400을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "데이터베이스 상에 문제가 있을 경우 500을 반환합니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    ResponseEntity<Void> evaluateRestaurant(
            @PathVariable Long restaurantId,
            EvaluationDTO evaluationDTO,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );
}
