package com.kustaurant.kustaurant.global.auth.session;

import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.UserRole;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        OAuthAttributes attrs = OAuthAttributes.of(
                userRequest.getClientRegistration().getRegistrationId(),
                userRequest.getClientRegistration()
                        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(),
                oauth2User.getAttributes());

        User user = userRepository.findByProviderId(attrs.getProviderId())
                .orElseGet(() -> {
                    String raw = StringUtils.substringBefore(attrs.getEmail(), "@");
                    Nickname nickname = new Nickname(StringUtils.left(raw, 10));

                    User newUser = User.builder()
                            .providerId(attrs.getProviderId())
                            .email(attrs.getEmail())
                            .nickname(nickname)
                            .role(UserRole.USER)
                            .loginApi(attrs.getLoginApi())
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });

        Set<SimpleGrantedAuthority> authSet =
                Set.of(new SimpleGrantedAuthority(user.getRole().getValue()));

        return new CustomOAuth2User(
                user.getId(),          // Integer
                authSet,               // Set<GrantedAuthority>
                attrs.getAttributes()  // extra info
        );
    }

    //리팩토링중 임시로 둘 코드
    public UserEntity getUser(String providerId) {
        return userRepository.findByProviderId(providerId)
                .map(UserEntity::from)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }
    //리팩토링중 임시로 둘 코드
    public UserEntity getUserByPrincipal(Principal principal) {
        Integer id = Integer.valueOf(principal.getName());
        return userRepository.findById(id)
                .map(UserEntity::from)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

}
