package com.kustaurant.kustaurant.global.webUser;

import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private Nickname Nickname;
    private String userEmail;
    private String userTokenId;
    private String loginApi;

    public SessionUser(UserEntity UserEntity) {
        this.Nickname = UserEntity.getUserNickname();
        this.userEmail = UserEntity.getUserEmail();
        this.userTokenId = UserEntity.getProviderId();
        this.loginApi = UserEntity.getLoginApi();
    }
}
