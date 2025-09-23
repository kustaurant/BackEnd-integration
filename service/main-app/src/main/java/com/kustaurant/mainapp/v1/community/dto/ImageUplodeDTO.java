package com.kustaurant.mainapp.v1.community.dto;

import lombok.Data;

@Data
public class ImageUplodeDTO {
    String imgUrl;
    public ImageUplodeDTO(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
