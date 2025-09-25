package com.kustaurant.kustaurant.user.login.api.provider;

import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.user.domain.User;

public interface LoginProcessor {
    boolean supports(LoginApi type);
    User handle(LoginRequest request);
}
