package com.kustaurant.restauranttier.common.user;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String userProviderId;
    private String loginApi;
    private String userEmail;
    private String nameAttributeKey;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String userProviderId,
                           String loginApi,
                           String userEmail,
                           String nameAttributeKey) {
        this.attributes = attributes;
        this.userProviderId = userProviderId;
        this.loginApi = loginApi;
        this.userEmail = userEmail;
        this.nameAttributeKey = nameAttributeKey;
    }

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        } else {
            return ofGoogle(userNameAttributeName, attributes);
        }
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .userEmail((String) response.get("email"))
                .loginApi("NAVER")
                .userProviderId((String) response.get("id"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .userProviderId((String) attributes.get("userTokenId"))
                .loginApi((String) attributes.get("loginApi"))
                .userEmail((String) attributes.get("userEmail"))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User webToEntity() {
        return User.builder()
                .naverProviderId(userProviderId)
                .loginApi(loginApi)
                .userEmail(userEmail)
                .userNickname(StringUtils.substringBefore(userEmail, "@"))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .userRole(UserRole.USER)
                .build();
    }
}
