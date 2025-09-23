package com.kustaurant.mainapp.global.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    /**
     * 블럭 내부 아무 코드도 없지만, JPA Auditing 기능을 On하기 위한 클래스.
     * (@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy 자동 세팅)
     */
}
