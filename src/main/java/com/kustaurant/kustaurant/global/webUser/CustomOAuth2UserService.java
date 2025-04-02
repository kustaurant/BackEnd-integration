package com.kustaurant.kustaurant.global.webUser;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final OUserRepository OUserRepository;
    private final HttpSession httpSesseion;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.
                of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        UserEntity UserEntity = saveOrUpdate(attributes);
        httpSesseion.setAttribute("user", new SessionUser(UserEntity));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(UserEntity.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private UserEntity saveOrUpdate(OAuthAttributes attributes) {
        UserEntity UserEntity = OUserRepository.findByProviderId(attributes.getUserProviderId())
                .map(entity -> entity.updateUserEmail(attributes.getUserEmail()))
                .orElse(attributes.webToEntity());

        return OUserRepository.save(UserEntity);
    }

    public UserEntity getUser(String userTokenId) {
        Optional<UserEntity> user = OUserRepository.findByProviderId(userTokenId);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new DataNotFoundException("user not found");
        }
    }

    public UserEntity getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        Optional<UserEntity> user = OUserRepository.findByProviderId(principal.getName());
        return user.orElse(null);
    }
}
