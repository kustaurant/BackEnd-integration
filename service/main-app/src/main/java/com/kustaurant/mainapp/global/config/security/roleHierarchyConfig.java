package com.kustaurant.mainapp.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class roleHierarchyConfig {
    /*
    Spring Security는 기본적으로 역할 간 상속 관계를 지원하지 않기 때문에
    예를 들어 ADMIN이 USER 권한을 포함하는 구조를 만들고자 할 경우 수동으로 계층을 등록해야 함

    그거 설정하는 config임.
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                "ROLE_ADMIN > ROLE_USER\n" +
                "ROLE_USER  > ROLE_GUEST"
        );
        return roleHierarchy;
    }
}
