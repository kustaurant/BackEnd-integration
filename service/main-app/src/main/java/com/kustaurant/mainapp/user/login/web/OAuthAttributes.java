package com.kustaurant.mainapp.user.login.web;

import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String providerId;
    private final LoginApi loginApi;
    private final String email;
    private final String nameAttributeKey;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String providerId,
                           LoginApi loginApi,
                           String email,
                           String nameAttributeKey) {
        this.attributes = attributes;
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.email = email;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuthAttributes of(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .loginApi(LoginApi.NAVER)
                .providerId((String) response.get("id"))
                .attributes(response)
                .nameAttributeKey("id")
                .build();
    }

}