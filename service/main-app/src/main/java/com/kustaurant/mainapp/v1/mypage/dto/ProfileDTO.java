package com.kustaurant.mainapp.v1.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDTO {
    private String nickname;
    private String email;
    private String phoneNumber;
}