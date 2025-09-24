package com.kustaurant.kustaurant.user.login.web;

import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(req);

        OAuthAttributes attrs = OAuthAttributes.of(oauth2User.getAttributes());

        User user = userRepository.findByProviderId(attrs.getProviderId())
                .orElseGet(() -> {
                    User newUser=User.createFromNaver(attrs);
                    return userRepository.save(newUser);
                });

        Set<SimpleGrantedAuthority> authSet = Set.of(new SimpleGrantedAuthority(user.getRole().getValue()));

        return new CustomOAuth2User(
                user.getId(),          // Long
                authSet,               // Set<GrantedAuthority>
                attrs.getAttributes()  // extra info
        );
    }
}
