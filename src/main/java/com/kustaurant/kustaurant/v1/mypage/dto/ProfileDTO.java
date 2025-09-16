package com.kustaurant.kustaurant.v1.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDTO {
    private String nickname;
    private String email;
    private String phoneNumber;
}