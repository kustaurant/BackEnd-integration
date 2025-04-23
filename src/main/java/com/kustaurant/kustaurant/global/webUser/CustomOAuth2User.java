package com.kustaurant.kustaurant.global.webUser;

import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user,
                            Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes) {
        this.user = user;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
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

