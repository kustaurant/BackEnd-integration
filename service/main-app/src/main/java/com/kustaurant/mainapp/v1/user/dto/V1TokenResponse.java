package com.kustaurant.mainapp.v1.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class V1TokenResponse {
    private String accessToken;
}
