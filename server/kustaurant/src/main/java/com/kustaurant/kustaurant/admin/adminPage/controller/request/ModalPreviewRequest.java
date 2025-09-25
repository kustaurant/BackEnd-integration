package com.kustaurant.kustaurant.admin.adminPage.controller.request;

public record ModalPreviewRequest (
        String title,
        String body,
        String expiredAt
) {}