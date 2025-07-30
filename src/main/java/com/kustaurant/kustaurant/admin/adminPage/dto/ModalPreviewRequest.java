package com.kustaurant.kustaurant.admin.adminPage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModalPreviewRequest {
    private String title;
    private String body;
    private String expiredAt;
}