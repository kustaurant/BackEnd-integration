package com.kustaurant.kustaurant.global.webUser;

import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSesseion;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 🔥 핵심 수정 부분
        User user = userRepository.findByProviderId(attributes.getProviderId())
                .orElseGet(() -> {
                    String nicknameCandidate = extractNickname(attributes.getEmail());
                    Nickname nickname = new Nickname(nicknameCandidate);
                    return User.builder()
                            .providerId(attributes.getProviderId())
                            .email(attributes.getEmail())
                            .nickname(nickname)
                            .role(UserRole.USER)
                            .loginApi(attributes.getLoginApi())
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .build();
                });

        userRepository.save(user);
        httpSesseion.setAttribute("user", new SessionUser(user)); // 세션 저장

        return new CustomOAuth2User(
                user,
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue())),
                attributes.getAttributes()
        );
    }

    private String extractNickname(String email) {
        String prefix = StringUtils.substringBefore(email, "@");
        return prefix.length() > 10 ? prefix.substring(0, 10) : prefix;
    }

    //리팩토링중 임시로 둘 코드
    public UserEntity getUser(String providerId) {
        return userRepository.findByProviderId(providerId)
                .map(UserEntity::from)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }
    //리팩토링중 임시로 둘 코드
    public UserEntity getUserByPrincipal(Principal principal) {
        return userRepository.findByProviderId(principal.getName())
                .map(UserEntity::from)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

}
