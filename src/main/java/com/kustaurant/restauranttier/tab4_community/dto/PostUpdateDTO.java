package com.kustaurant.restauranttier.tab4_community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class PostUpdateDTO {
    @Schema(description = "수정할 게시글 제목입니다. 최소 1자에서 최대 100자까지 입력 가능합니다.", example = "업데이트된 맛집 추천")
    private String title;

    @Schema(description = "수정할 게시판 종류입니다. (free: 자유게시판, column: 칼럼게시판, suggestion: 건의게시판)", example = "free")
    private String postCategory;

    @Schema(description = "수정할 게시글의 내용입니다. 최소 1자에서 최대 10,000자까지 입력 가능합니다.", example = "건대 맛집을 다녀왔습니다.")
    private String content;

    @Schema(description = "String 타입의 이미지 파일입니다. 이미지 업로드 api를 사용하여 반환받은 String 타입의 이미지 url을 게시글 작성에 첨부합니다. 이 필드는 생략 가능합니다.")
    private String imageFile;}