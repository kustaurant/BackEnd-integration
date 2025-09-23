package com.kustaurant.mainapp.user.login.web;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
public final class CustomOAuth2User implements OAuth2User, Serializable {

    private final Long userId;
    private final Set<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Long userId,
                            Set<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes) {
        this.userId = userId;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}

