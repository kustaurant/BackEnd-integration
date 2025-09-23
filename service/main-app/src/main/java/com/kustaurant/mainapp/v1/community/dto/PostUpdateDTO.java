package com.kustaurant.mainapp.v1.community.dto;

import lombok.Data;

@Data
public class PostUpdateDTO {
    private String title;
    private String postCategory;
    private String content;
}