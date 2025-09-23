package com.kustaurant.mainapp.user.mypage.controller.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(min = 2, max = 10, message = "닉네임은 2~10자여야 합니다.")
        String nickname,

        @Pattern(
                regexp = "^01[016789]\\d{8}$",
                message = "전화번호는 숫자로만 11자리여야 합니다.('-'제외)"
        )
        String phoneNumber
) {}
