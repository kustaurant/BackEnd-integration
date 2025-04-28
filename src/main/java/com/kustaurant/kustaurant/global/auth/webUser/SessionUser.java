package com.kustaurant.kustaurant.global.auth.webUser;

import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String nickname;
    private String email;
    private String tokenId;
    private String loginApi;

    public SessionUser(User user) {
        this.nickname = user.getNickname().getValue();
        this.email = user.getEmail();
        this.tokenId = user.getProviderId();
        this.loginApi = user.getLoginApi();
    }
}
