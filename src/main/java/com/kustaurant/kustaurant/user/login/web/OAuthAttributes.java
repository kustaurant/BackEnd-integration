package com.kustaurant.kustaurant.user.login.web;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String providerId;
    private final String loginApi;
    private final String email;
    private final String nameAttributeKey;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String providerId,
                           String loginApi,
                           String email,
                           String nameAttributeKey) {
        this.attributes = attributes;
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.email = email;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuthAttributes of(
            String registrationId,
            String userNameAttributeName,
            Map<String, Object> attributes
    ) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        } else {
            return ofGoogle(userNameAttributeName, attributes);
        }
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(
            String userNameAttributeName,
            Map<String, Object> attributes
    ) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .loginApi("NAVER")
                .providerId((String) response.get("id"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .providerId((String) attributes.get("userTokenId"))
                .loginApi((String) attributes.get("loginApi"))
                .email((String) attributes.get("userEmail"))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }


}
