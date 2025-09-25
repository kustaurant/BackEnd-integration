package com.kustaurant.kustaurant.global.auth.argumentResolver;

import com.kustaurant.kustaurant.user.login.web.CustomOAuth2User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.global.exception.exception.auth.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class)
                && AuthUserInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter p,
                                  ModelAndViewContainer m,
                                  NativeWebRequest w,
                                  WebDataBinderFactory b) {

        /* ---------- 세션(Web) + JWT(App) 공통 처리 ---------- */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthenticatedException("SecurityContext 인증 객체가 없거나 비활성화됨");
        }

        /* ───────────── principal → userId 추출 ───────────── */
        Long userId;
        if (auth.getPrincipal() instanceof CustomOAuth2User oAuthUser) {        // 웹 세션
            userId = oAuthUser.getUserId();
        } else if (auth.getPrincipal() instanceof Long uid) {                // 앱 JWT
            userId = uid;
        } else if (auth.getPrincipal() instanceof String s && "anonymousUser".equals(s)) {
            return new AuthUserInfo(null, UserRole.GUEST);
        }else {
            throw new UnauthenticatedException("지원하지 않는 principal 타입: " + auth.getPrincipal().getClass().getName());
        }

        /* ───────────── 권한(ROLE) 추출 및 매핑 ───────────── */
        UserRole role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)  // "ROLE_USER"
                .map(UserRole::from)                  // enum 매핑
                .orElse(UserRole.USER);

        return new AuthUserInfo(userId, role);   // id·role 최소 DTO
    }
}
