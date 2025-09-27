package com.kustaurant.kustaurant.user.mypage.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
        @Schema(description = "닉네임은 어떤 문자도 모두 허용하며 단, 2~10자 여야 합니다.")
        String nickname,

        @Pattern(
                regexp = "^01[016789]\\d{8}$",
                message = "전화번호는 숫자로만 11자리여야 합니다.('-'제외)"
        )
        @Schema(description = "전화번호는 - 없이 숫자로만 11자리 입니다.")
        String phoneNumber
) {}
