/**
 * flyway 마이그레이션 실행하다가 이전 실패 이력 등이 남는 경우, 정상작동 안하게됨.
 * 그때 해당부분 주석 풀어서 실행해주면 됨.
 *
 * 주의 :: ❌ 운영에선 절대 repair 실행 금지
 */

package com.kustaurant.kustaurant.global.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class flywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return (Flyway flyway) -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
