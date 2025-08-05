package com.kustaurant.kustaurant.post.post.controller.api;


import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostDTO;
import com.kustaurant.kustaurant.post.post.controller.response.UserDTO;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.comment.service.PostCommentApiService;
import com.kustaurant.kustaurant.post.post.service.PostQueryApiService;
import com.kustaurant.kustaurant.post.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommunityApiController {
    private final PostQueryApiService postQueryApiService;
    private final PostCommentApiService postCommentApiService;
    private final PostService postCommandService;

    // 1. 커뮤니티 메인 화면
    @GetMapping("/api/v2/community/posts")
    @Operation(
            summary = "커뮤니티 메인화면의 게시글 리스트 불러오기",
            description = "게시판 종류와 페이지, 정렬 방법을 입력받고 해당 조건에 맞는 게시글 리스트가 반환됩니다, 현재 인기순으로 설정했을 때는 좋아요가 3이상인 게시글만 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트를 반환하는데 성공하였습니다.", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = PostDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "요청한 조건의 게시글을 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "파라미터 값이 유효하지 않습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    public ResponseEntity<List<PostDTO>> community(
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "FREE",
                    description = "게시판 종류(ALL:전체, FREE:자유게시판, COLUMN:칼럼게시판, SUGGESTION:건의게시판)")
            PostCategory category,

            @Parameter(example = "0",
                    description = "게시글은 페이지 단위로 불러올 수 있고, 한 페이지에 10개의 게시글이 담겨있습니다. 페이지 인덱스는 0부터 시작합니다.")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @RequestParam(defaultValue = "LATEST")
            @Parameter(description = "정렬 방식 (LATEST=최신순, POPULARITY=인기순)", example = "POPULARITY")
            SortOption sort,

            @RequestParam(defaultValue = "html")
            @Parameter(example = "text", description = "반환되는 게시글 내용의 타입입니다. (html,text)") String postBodyType
    ) {
        // Enum으로 변환하고 한글 이름 추출
        Page<PostDTO> paging = postQueryApiService.getPosts(page, sort, category, postBodyType);

        return ResponseEntity.ok(paging.getContent());
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/api/v1/community/{postId}")
    @Operation(summary = "게시글 상세 화면", description = "게시글 ID를 받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> post(
            @PathVariable @Parameter(description = "게시글 id", example = "69") Integer postId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        postQueryApiService.validatePostId(postId);
        Post post = postQueryApiService.getPost(postId);
        postCommandService.increaseVisitCount(postId);
        return ResponseEntity.ok(postCommentApiService.createPostDTOWithFlags(post,user.id()));
    }

    // 3. 커뮤니티 메인 유저랭킹
    @GetMapping("/api/v1/community/ranking")
    @Operation(summary = "커뮤니티 메인의 랭킹 탭에서 유저 랭킹 불러오기", description = "평가 수 기반의 유저 랭킹을 반환합니다. 분기순, 최신순으로 랭킹을 산정할 수 있습니다. 평가를 1개 이상 한 유저들은 모두 랭킹이 매겨집니다.")
    @ApiResponse(responseCode = "200", description = "유저 랭킹을 반환하는데 성공하였습니다", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))})
    @ApiResponse(responseCode = "404", description = "sort 파라미터 값이 잘못되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public List<UserDTO> ranking(
            @RequestParam @Parameter(description = "랭킹 산정 기준입니다. 분기순:quarterly, 최신순:cumulative", example = "cumulative") String sort
    ) {

        List<UserDTO> userList = postQueryApiService.getUserListforRanking(sort);
        return userList;
    }


}
