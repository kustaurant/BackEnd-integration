package com.kustaurant.kustaurant.common.user.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String nickname;
    private String email;
    private String phoneNumber;
}