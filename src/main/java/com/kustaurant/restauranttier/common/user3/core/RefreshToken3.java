package com.kustaurant.restauranttier.common.user3.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class RefreshToken  implements Serializable {

    @Id
    private String id;

    private String accessToken;

    private String refreshToken;

    public void updateAccessToken(String accessToken) {
        this.accessToken=accessToken;
    }
}
