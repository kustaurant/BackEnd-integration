package com.kustaurant.restauranttier.tab4_community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ImageUplodeDTO {
        public ImageUplodeDTO(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Schema(description = "이미지 주소", example = "https://kustaurant.s3.ap-northeast-2.amazonaws.com/community/%EC%BF%A0.jpg")
        String imgUrl;
}
