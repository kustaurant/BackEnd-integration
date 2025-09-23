package com.kustaurant.mainapp.user.login.api.provider;

import com.kustaurant.mainapp.user.login.api.controller.LoginRequest;
import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import com.kustaurant.mainapp.user.user.domain.User;

public interface LoginProcessor {
    boolean supports(LoginApi type);
    User handle(LoginRequest request);
}
