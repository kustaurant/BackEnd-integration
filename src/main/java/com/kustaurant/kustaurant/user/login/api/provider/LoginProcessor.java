package com.kustaurant.kustaurant.user.login.api.provider;

import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.ProviderType;
import com.kustaurant.kustaurant.user.user.domain.User;

public interface LoginProcessor {
    boolean supports(ProviderType type);
    User handle(LoginRequest request);
}
