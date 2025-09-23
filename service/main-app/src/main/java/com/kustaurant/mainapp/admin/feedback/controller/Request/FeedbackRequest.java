package com.kustaurant.mainapp.admin.feedback.controller.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record FeedbackRequest(
        @NotBlank(message = "내용이 없습니다.")
        @Size(min = 10, max = 500, message = "내용은 10자 이상 500자 이하여야 합니다.")
        String comment
) {}